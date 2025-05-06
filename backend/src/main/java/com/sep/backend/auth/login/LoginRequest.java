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

    @Email
    @NotBlank
    @Schema(description = "The email of the user.")
    private String email;

    @NotBlank
    @Schema(description = "The password of the user.")
    private String password;
}
