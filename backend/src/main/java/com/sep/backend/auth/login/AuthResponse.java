package com.sep.backend.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the response to the auth request.")
public class AuthResponse {

    @Schema(description = "The access token for the user.", requiredMode = RequiredMode.REQUIRED)
    private String accessToken;

}
