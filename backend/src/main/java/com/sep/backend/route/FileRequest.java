package com.sep.backend.route;

import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Represents a GeoJSON File.")

public class FileRequest {

    private MultipartFile file;

}
