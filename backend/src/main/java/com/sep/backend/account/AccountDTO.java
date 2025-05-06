package com.sep.backend.account;

import com.sep.backend.entity.Rating;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "Represents the data of an account.", requiredProperties = {"username", "email", "role", "firstName", "lastName", "birthday", "totalNumberOfRides","ratings"})
public class AccountDTO {
    @Schema(description = "The unique username of the account.", example = "john_doe")
    private String username;

    @Schema(description = "The email address of the account.", example = "john.doe@example.com")
    private String email;

    @Schema(description = "The role of the user ( admin, user).", example = "user")
    private String role;

    @Schema(description = "First name of the account holder.", example = "John")
    private String firstName;

    @Schema(description = "Last name of the account holder.", example = "Doe")
    private String lastName;

    @Schema(description = "URL to the profile picture (optional).", nullable = true)
    private String profilePictureUrl;

    @Schema(description = "The type of car associated with the account when the person is a driver  .", nullable = true)
    private CarType carType;

    @Schema(description = "Date of birth in the format YYYY-MM-DD.", example = "1985-05-15")
    private String birthday;

    @Schema(description = "List of ratings associated with this account .", nullable = true)
    private List<Rating> ratings;

    @Schema(description = "The total number of rides the account holder has taken.", example = "34")
    private int totalNumberOfRides;
}