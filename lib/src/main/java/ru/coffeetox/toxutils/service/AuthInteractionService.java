package ru.coffeetox.toxutils.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestClient;
import ru.coffeetox.toxutils.dto.auth.AccountPublicDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class AuthInteractionService {

    private final RestClient authRestClient;

    public AuthInteractionService(@Qualifier("authRestClient") RestClient authRestClient) {
        this.authRestClient = authRestClient;
    }

    public Map<UUID, AccountPublicDto> populateAccounts(List<UUID> ids) {
        return authRestClient
                .get()
                .uri("/account/populate")
                .attribute("ids", ids)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<UUID, AccountPublicDto>>() {});
    }

}
