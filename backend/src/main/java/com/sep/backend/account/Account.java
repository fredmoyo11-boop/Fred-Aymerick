package com.sep.backend.account;

import com.sep.backend.Roles;
import com.sep.backend.entity.AccountEntity;
import com.sep.backend.entity.CustomerEntity;
import com.sep.backend.entity.DriverEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents the information of an account.")
public class Account {

    @Schema(description = "The username of the account.", requiredMode = RequiredMode.REQUIRED)
    private String username;

    @Schema(description = "The role of the account.", requiredMode = RequiredMode.REQUIRED)
    private String role;

    @Schema(description = "The first name of the user.", requiredMode = RequiredMode.REQUIRED)
    private String firstName;

    @Schema(description = "The last name of the user.", requiredMode = RequiredMode.REQUIRED)
    private String lastName;

    @Schema(description = "The email of the user.", requiredMode = RequiredMode.REQUIRED)
    private String email;

    @Schema(description = "The birth date of the user.", requiredMode = RequiredMode.REQUIRED)
    private LocalDate birthDate;

    @Schema(description = "The url to the profile picture. Might be null if user has no profile picture.", requiredMode = RequiredMode.NOT_REQUIRED)
    private String profilePictureUrl;

    @Schema(description = "The rating of the user.", requiredMode = RequiredMode.REQUIRED)
    private Double rating;

    @Schema(description = "The count of trips the user has made.", requiredMode = RequiredMode.REQUIRED)
    private Integer tripCount;

    @Schema(description = "The (optional) additional driver information. Might be null if user is CUSTOMER.", requiredMode = RequiredMode.NOT_REQUIRED)
    private Driver driver;

    private static Account from(AccountEntity entity) {
        var account = new Account();
        account.setUsername(entity.getUsername());
        account.setFirstName(entity.getFirstName());
        account.setLastName(entity.getLastName());
        account.setEmail(entity.getEmail());
        account.setBirthDate(entity.getBirthday());
        account.setProfilePictureUrl(entity.getProfilePictureUrl());
        // update when logic available
        account.setRating(3.5);
        account.setTripCount(12);
        return account;
    }

    public static Account fromCustomer(CustomerEntity entity) {
        var account = Account.from(entity);
        account.setRole(Roles.CUSTOMER);
        return account;
    }

    public static Account fromDriver(DriverEntity entity) {
        var account = Account.from(entity);
        account.setRole(Roles.DRIVER);
        account.setDriver(new Driver(entity.getCarType()));
        return account;
    }
}
