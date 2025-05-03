package com.sep.backend.triprequest;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a request for a drive.")
public class TripRequestDTO {

    @NotBlank
    @Schema(description = "The username of the customer.", requiredMode = RequiredMode.REQUIRED)
    private String username;

    @NotBlank
    @Schema(description = "The start location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private String startAddress;

    @NotBlank
    @Schema(description = "The end location of the drive.", requiredMode = RequiredMode.REQUIRED)
    private String endAddress;

    @NotBlank
    @Schema(description = "The type of car requested.", requiredMode = RequiredMode.REQUIRED)
    private String carType;

    @Schema(description = "Optional notes by customer", requiredMode = RequiredMode.NOT_REQUIRED)
    private String note;
}
