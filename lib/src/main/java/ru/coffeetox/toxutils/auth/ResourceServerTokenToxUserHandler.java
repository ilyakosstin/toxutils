package ru.coffeetox.toxutils.auth;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.nio.file.AccessDeniedException;
import java.util.UUID;
import java.util.regex.Pattern;

public class ResourceServerTokenToxUserHandler implements HandlerMethodArgumentResolver {

    private static final Pattern STRICT_UUID_REGEX =
            Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

    private boolean isCorrectUUID(String uuidStr) {
        return STRICT_UUID_REGEX.matcher(uuidStr).matches();
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(ToxUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            return null;
        }

        Object principal = authentication.getPrincipal();

        if (principal == null) {
            return null;
        }

        String uuidStr = switch(principal) {
            case OAuth2User oauth2User -> {
                Object userIdAttr = oauth2User.getAttribute("userid");
                yield userIdAttr == null? null : userIdAttr.toString();
            }
            case Jwt jwt -> jwt.getSubject();
            default -> null;
        };

        if (uuidStr == null) {
            return null;
        } else if(!isCorrectUUID(uuidStr)) {
            throw new AccessDeniedException("Invalid UUID was provided");
        }

        return new ToxUser(UUID.fromString(uuidStr));
    }
}
