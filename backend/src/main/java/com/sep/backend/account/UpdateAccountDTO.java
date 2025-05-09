package com.sep.backend.account;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
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

}
