package com.sep.backend.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateAccountDTO {
    private String username;
    private String firstName;
    private String  lastName;
    private String birthday;
    private CarType carType;

    @JsonIgnore
    private MultipartFile profilePicture;
}
