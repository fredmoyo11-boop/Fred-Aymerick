package com.sep.backend.route;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a Route Request.")
public class RouteDTO {

    @NotBlank
    @Schema(description = "The id of the route.", requiredMode = RequiredMode.REQUIRED)
    private long routeid;

}