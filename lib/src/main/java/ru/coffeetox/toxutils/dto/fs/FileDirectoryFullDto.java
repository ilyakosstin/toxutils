package ru.coffeetox.toxutils.dto.fs;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Data
@Builder
public class FileDirectoryFullDto {

    private UUID id;
    private UUID ownerId;
    private String name;
    private Instant createdAt;
    private Boolean isPublic;
    private List<FileMetadataDto> innerFiles;

}
