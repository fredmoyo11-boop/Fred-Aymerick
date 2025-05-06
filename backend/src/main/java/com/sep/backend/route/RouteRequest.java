package com.sep.backend.route;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a Route request.")
public class RegistrationRequest {
//
//    @Schema(description = "The user data.", implementation = RegistrationDTO.class, requiredMode = RequiredMode.REQUIRED)
//    private RegistrationDTO data;
//
//    @Schema(description = "The profile picture of the user.", requiredMode = RequiredMode.NOT_REQUIRED)
//    private MultipartFile file;
}
