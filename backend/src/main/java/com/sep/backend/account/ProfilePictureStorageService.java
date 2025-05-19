package com.sep.backend.account;

import com.sep.backend.ErrorMessages;
import com.sep.backend.auth.registration.RegistrationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class ProfilePictureStorageService {

    private final Logger log = LoggerFactory.getLogger(ProfilePictureStorageService.class);

    private final FileStorageProperties properties;
    private final Path root;


    public ProfilePictureStorageService(FileStorageProperties properties) {
        this.properties = properties;
        this.root = Paths.get(properties.getUploadDir() + "/profile/picture/");
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * Saves the profile picture file for the given username, replaces old picture if exists
     *
     * @param file     The profile picture file
     * @param username The username
     */
    public String save(MultipartFile file, String username) {
        try {
            String fileExtension = getFileExtension(file);
            String fileName = username.toLowerCase() + "." + fileExtension;

            Path destination = root
                    .resolve(Paths.get(fileName))
                    .normalize()
                    .toAbsolutePath();

            log.debug("Saving new profile picture of {}", username);
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, destination, StandardCopyOption.REPLACE_EXISTING);
            }
            log.info("Saved new profile picture of {}", username);
            String url = "http://localhost:8080/uploads/profile/picture/" + username + "." + fileExtension;
            log.debug("Profile picture of {} available at {}", username, url);
            return url;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts the extension of image files.
     *
     * @param file The image file.
     * @return The file extension
     * @throws RegistrationException If image file is not jpeg or png.
     */
    private String getFileExtension(MultipartFile file) throws RegistrationException {
        String contentType = file.getContentType();
        if (contentType == null) return null;
        return switch (contentType) {
            case MediaType.IMAGE_JPEG_VALUE -> "jpeg";
            case MediaType.IMAGE_PNG_VALUE -> "png";
            default -> throw new RegistrationException(ErrorMessages.INVALID_PROFILE_PICTURE_FORMAT);
        };
    }
}
