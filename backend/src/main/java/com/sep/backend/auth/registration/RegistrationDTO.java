package com.sep.backend.auth.registration;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents the common registration data of a user.")
public class RegistrationDTO {

    @Email
    @NotBlank
    @Schema(description = "The email of the user.", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Schema(description = "The username of the user.", requiredMode = RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Schema(description = "The password of the user.", requiredMode = RequiredMode.REQUIRED)
    private String password;

    @NotBlank
    @Schema(description = "The firstname of the user.", requiredMode = RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank
    @Schema(description = "The lastname of the user.", requiredMode = RequiredMode.REQUIRED)
    private String lastName;

    @NotNull
    @Schema(description = "The birthday of the user.", requiredMode = RequiredMode.REQUIRED)
    private String birthday;

    @NotBlank
    @Schema(description = "The role of the user. Either CUSTOMER or DRIVER.", requiredMode = RequiredMode.REQUIRED)
    private String role;

    @Schema(description = "The type of car associated only  with the driver account" , allowableValues = {"SMALL", "MEDIUM", "DELUXE"},
            requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String carType;

}
