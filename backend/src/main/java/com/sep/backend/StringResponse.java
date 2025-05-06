package com.sep.backend;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A wrapper for a response containing only a string.")
public class StringResponse {

    @Schema(description = "The string of the response.", requiredMode = RequiredMode.REQUIRED)
    private String message;

}
