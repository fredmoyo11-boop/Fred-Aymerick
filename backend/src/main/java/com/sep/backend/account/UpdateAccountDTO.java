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
    @Schema(description = "Benutzername des Accounts", example = "john_doe", required = false)
    private String username;

    @Schema(description = "Vorname des Account-Inhabers", example = "John", required = false)
    private String firstName;

    @Schema(description = "Nachname des Account-Inhabers", example = "Doe", required = false)
    private String lastName;

    @Schema(description = "Geburtsdatum im Format YYYY-MM-DD", example = "1990-01-01", required = false)
    private String birthday;

    @Schema(description = "Typ des Autos", required = false)
    private CarType carType;

    @JsonIgnore
    @Schema(description = "Profilbild des Nutzers ", hidden = true, required = false)
    private MultipartFile profilePicture;

}
