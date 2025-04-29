package com.sep.backend.account;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AccountupdateDTO {
    private String username;
    private String firstName;
    private String  lastName;
    private MultipartFile profilePicture;
}
