package com.sep.backend.account;

import com.sep.backend.entity.Rating;
import com.sep.backend.triprequest.CarType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Represents the data of an account.", requiredProperties = {"username", "email", "role", "firstName", "lastName", "birthday", "totalNumberOfRides","ratings"})
public class AccountDTO {

    @NotBlank
    @Schema(description = "The unique username of the account.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Email
    @Schema(description = "The email address of the account.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Schema(description = "The role of the user ( 'CUSTOMER' or 'DRIVER' ).", requiredMode = Schema.RequiredMode.REQUIRED)
    private String role;

    @NotBlank
    @Schema(description = "First name of the account holder.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String firstName;

    @NotBlank
    @Schema(description = "Last name of the account holder.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "URL to the profile picture (optional).",requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String profilePictureUrl;

    @Schema(description = "The type of car associated with the account (when the person is a driver its cannot be null). " + "Should be null if the user is a customer.", allowableValues = {"SMALL", "MEDIUM", "DELUXE"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String carType;

    @NotBlank
    @Schema(description = "Date of birth in the format YYYY-MM-DD." ,requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String birthday;

    @NotNull
    @Schema(description = "List of ratings associated with this account .",requiredMode = Schema.RequiredMode.REQUIRED)
    private List<Rating> ratings;

    @NotNull
    @Schema(description = "The total number of rides the account holder has taken.",requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalNumberOfRides;
}