package ru.coffeetox.toxutils.service;

import org.apache.commons.logging.Log;import org.apache.commons.logging.LogFactory;import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.multipart.MultipartFile;
import ru.coffeetox.toxutils.auth.ToxUser;
import ru.coffeetox.toxutils.dto.fs.FileDirectoryCompactDto;
import ru.coffeetox.toxutils.dto.fs.FileDirectoryFullDto;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class FsInteractionService {
    private static final Log log = LogFactory.getLog(FsInteractionService.class);private final RestClient fsRestClient;

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

        String uri = user == null? "/directory" : "/directory/for/" + user.getUserId();

        Map<String, Object> directoryInfo = fsRestClient.post()
                .uri(uri)
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

        String uri = user == null? "/upload/to/" + directoryId : "/upload/to/" + directoryId + "/for/" + user.getUserId();

        List<Map<String, Object>> fileMetadata = fsRestClient.post()
                .uri(uri)
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

    public Map<UUID, FileDirectoryCompactDto> populateDirectoriesCompact(List<UUID> ids) {
        return fsRestClient.get()
                .attribute("directories", ids)
                .attribute("full", false)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<UUID, FileDirectoryCompactDto>>() {});
    }

    public Map<UUID, FileDirectoryFullDto> populateDirectoriesFull(List<UUID> ids) {
        return fsRestClient.get()
                .attribute("directories", ids)
                .attribute("full", true)
                .retrieve()
                .body(new ParameterizedTypeReference<Map<UUID, FileDirectoryFullDto>>() {});
    }

    public void deleteDirectory(UUID directoryId) {
        log.info("deleting " + directoryId);
    }

}
