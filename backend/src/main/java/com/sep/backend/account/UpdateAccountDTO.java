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

@Schema(description = "Represents the data of an account that can be updated.")
public class UpdateAccountDTO {
    @Schema(description = "Benutzername des Accounts", example = "john_doe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    @Schema(description = "Vorname des Account-Inhabers", example = "John", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String firstName;

    @Schema(description = "Nachname des Account-Inhabers", example = "Doe", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lastName;

    @Schema(description = "Geburtsdatum im Format YYYY-MM-DD", example = "1990-01-01", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String birthday;


    @Schema(description = "Typ des Autos nur bei drivers ",allowableValues = {"SMALL", "MEDIUM", "DELUXE"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private CarType carType;

    @JsonIgnore
    @Schema(description = "Profilbild des Nutzers ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile profilePicture;
}
