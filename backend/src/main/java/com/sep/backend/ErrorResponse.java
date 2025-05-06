package com.sep.backend;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents an error response with status code and message.")
public class ErrorResponse {
    @Schema(description = "The status code of the error response.")
    private int statusCode;

    @Schema(description = "The message of the error response.")
    private String message;
}
