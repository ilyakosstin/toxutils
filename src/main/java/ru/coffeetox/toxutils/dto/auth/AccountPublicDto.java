package ru.coffeetox.toxutils.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
public class AccountPublicDto {

    public UUID id;
    public String username;
    public String profileName;
    private UUID avatarFileDirectoryId;

}