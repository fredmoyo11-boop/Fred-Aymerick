package com.sep.backend.account;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Represents additional information of a driver.")
public class Driver {

    @Schema(description = "The car type the driver drives.", requiredMode = RequiredMode.REQUIRED)
    private String carType;

}
