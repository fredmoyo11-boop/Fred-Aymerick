package com.sep.backend.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "Represents a login auth request.")
public class LoginRequest {

    @Schema(description = "The unique identifier of the user. Either the email or the username.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String uniqueIdentifier;

    @NotBlank
    @Schema(description = "The password of the user.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
