package com.sep.backend;

import com.sep.backend.account.FileStorageProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {
    private final FileStorageProperties fileStorageProperties;

    public StaticResourceConfig(FileStorageProperties fileStorageProperties) {
        this.fileStorageProperties = fileStorageProperties;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadDir = fileStorageProperties.getUploadDir();
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().toUri().toString();

        registry.addResourceHandler("/uploads/**").addResourceLocations(uploadPath);
    }

}
