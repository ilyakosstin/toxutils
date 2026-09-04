package ru.coffeetox.toxutils.dto.fs;
import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class FileDirectoryCompactDto {
    private UUID id;
    private UUID ownerId;
    private String name;
    private Boolean isPublic;
    private long nFiles;
}
