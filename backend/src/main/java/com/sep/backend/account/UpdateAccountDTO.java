package com.sep.backend.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "The profile picture of the account.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonIgnore
    private MultipartFile profilePicture;
}
