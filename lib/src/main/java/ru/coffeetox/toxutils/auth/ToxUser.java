package ru.coffeetox.toxutils.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@AllArgsConstructor
@Getter
@Builder
public class ToxUser {

    // Used to unite authentication completed
    // using OAuth2 client (that is used on non /api endpoints)
    // using OAuth2 resource server (that is used on /api endpoints)

    private UUID userId;

}
