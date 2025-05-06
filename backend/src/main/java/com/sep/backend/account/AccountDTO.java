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

@Schema(description = "The Profil of the account.", requiredMode = Schema.RequiredMode.REQUIRED)
public class AccountDTO {
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    @Schema(description = "The car type of the account.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CarType carType;
    private String birthday;
    private List<Rating> ratings;
    private  int totalNumberOfRides;

}
