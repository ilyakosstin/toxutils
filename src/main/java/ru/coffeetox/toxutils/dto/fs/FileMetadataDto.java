package ru.coffeetox.toxutils.dto.fs;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class FileMetadataDto {
    private UUID id;
    private UUID ownerId;
    private String originalName;
    private String mimeType;
    private Long contentLength;
    private Instant createdAt;
}
