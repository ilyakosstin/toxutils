package ru.coffeetox.toxutils.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import ru.coffeetox.toxutils.auth.ToxUser;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FsInteractionService {
    private final RestClient fsRestClient;

    public FsInteractionService(
            @Qualifier("fsRestClient") RestClient fsRestClient
    ) {
        this.fsRestClient = fsRestClient;
    }

    private ByteArrayResource forwardMultipartFile(MultipartFile file) throws IOException {
        return new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        };
    }

    public UUID createDirectory(ToxUser user, String name, boolean isPublic, List<MultipartFile> files) throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("directoryName", name);
        body.add("isPublic", isPublic);

        for(MultipartFile file : files) {
            body.add("files", forwardMultipartFile(file));
        }

        Map<String, Object> directoryInfo = fsRestClient.post()
                .uri("/directory/for/" + user.getUserId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<String, Object>>() {});

        if(directoryInfo == null) {
            throw new IllegalStateException("Received directory info is null!");
        }

        return UUID.fromString((String)directoryInfo.get("id"));
    }

    public List<UUID> uploadToDirectory(ToxUser user, UUID directoryId, List<MultipartFile> files) throws Exception {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for(MultipartFile file : files) {
            body.add("files", forwardMultipartFile(file));
        }

        List<Map<String, Object>> fileMetadata = fsRestClient.post()
                .uri("/upload/to/" + directoryId + "/for/" + user.getUserId())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

        if(fileMetadata == null) {
            throw new IllegalStateException("Received directory info is null!");
        }

        return fileMetadata.stream()
                .map(m -> UUID.fromString((String)m.get("id")))
                .toList();
    }

}
