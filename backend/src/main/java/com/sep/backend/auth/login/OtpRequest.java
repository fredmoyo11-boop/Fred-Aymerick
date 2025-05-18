package com.sep.backend.auth.login;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "The final request with the OTP.")
public class OtpRequest {

    @NotBlank
    @Schema(description = "The unique identifier of the user.", requiredMode = RequiredMode.REQUIRED)
    private String uniqueIdentifier;

    @Size(min = 6, max = 6)
    @Schema(description = "The OTP of the user.", requiredMode = RequiredMode.REQUIRED)
    private String otp;

}
