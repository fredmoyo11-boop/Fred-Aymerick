package com.sep.backend.account;

import com.sep.backend.entity.Rating;
import lombok.*;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountDTO {
    private String username;
    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private String profilePictureUrl;
    private CarType carType;
    private String birthday;
    private List<Rating> ratings;
    private  int Totalnumberofrides;

}
