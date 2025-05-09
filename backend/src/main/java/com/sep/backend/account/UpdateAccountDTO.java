package com.sep.backend.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sep.backend.triprequest.CarType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.annotation.MultipartConfig;
import lombok.*;
import org.postgresql.shaded.com.ongres.stringprep.ProfileName;
import org.springframework.web.multipart.MultipartFile;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Schema(description = "Represents the data of an account that can be updated.")
public class UpdateAccountDTO {
    @Schema(description = "Benutzername des Accounts", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String username;

    @Schema(description = "Vorname des Account-Inhabers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String firstName;

    @Schema(description = "Nachname des Account-Inhabers", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String lastName;

    @Schema(description = "Geburtsdatum im Format YYYY-MM-DD",  requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String birthday;

    @Schema(description = "Typ des Autos nur bei drivers ",allowableValues = {"SMALL", "MEDIUM", "DELUXE"}, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String  carType;

    @JsonIgnore
    @Schema(description = "Profilbild des Nutzers ", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private MultipartFile profilePicture;
}
