package com.sep.backend.route;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import com.sep.backend.route.RouteDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a Route request.")
public class RouteRequest {

    @Schema(description = "The route id.", implementation = RouteDTO.class, requiredMode = RequiredMode.REQUIRED)
    private RouteDTO id;
}
