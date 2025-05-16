package com.sep.backend.auth;

import com.sep.backend.HttpStatus;
import com.sep.backend.StringResponse;
import com.sep.backend.Tags;
import com.sep.backend.auth.login.*;
import com.sep.backend.auth.registration.RegistrationDTO;
import com.sep.backend.auth.registration.RegistrationRequest;
import com.sep.backend.auth.registration.RegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthController {
    private final LoginService loginService;
    private final RegistrationService registrationService;

    public AuthController(LoginService loginService, RegistrationService registrationService) {
        this.loginService = loginService;
        this.registrationService = registrationService;
    }

    @GetMapping("/health")
    @Operation(description = "Returns the status of the auth controller.",
            tags = {Tags.AUTH},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Auth healthy.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse health() {
        return new StringResponse("OK");
    }

    @PostMapping("/register")
    @Operation(description = "Registers a new customer or driver.",
            tags = {Tags.AUTH},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Registration process started successfully.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.UNAUTHORIZED, description = "Username already exists."),
                    @ApiResponse(responseCode = HttpStatus.UNAUTHORIZED, description = "Email already exists.")},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(implementation = RegistrationRequest.class)
                    )
            ))
    public StringResponse register(@Parameter(description = "The registration data.") @RequestPart("data") RegistrationDTO data,
                                   @Parameter(description = "The (optional) profile picture file.") @RequestPart(value = "file", required = false) MultipartFile file) {
        return new StringResponse(registrationService.register(data, file));
    }

    @PostMapping("/login")
    @Operation(description = "Logs a user in.",
            tags = {Tags.AUTH},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Login process started successfully.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class))),
                    @ApiResponse(responseCode = HttpStatus.NOT_FOUND, description = "Customer or driver does not exist."),
                    @ApiResponse(responseCode = HttpStatus.UNAUTHORIZED, description = "Invalid credentials.")})
    public StringResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return new StringResponse(loginService.login(loginRequest));
    }

    @PostMapping("/refresh")
    @Operation(description = "Return a fresh access token.",
            tags = {Tags.AUTH},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Refreshed access token.",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class)))})
    public AuthResponse refresh(HttpServletRequest req) {
        return loginService.refresh(req);
    }

    @PostMapping("/verify/otp")
    @Operation(description = "Verifies an OTP.",
            tags = {Tags.AUTH},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "User verified successfully.",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class)))})
    public AuthResponse verifyOtp(@RequestBody @Valid OtpRequest otpRequest, HttpServletResponse res) {
        return loginService.verifyOtp(otpRequest, res);
    }


    @PostMapping("/verify/email")
    @Operation(description = "Verifies a user registration.",
            tags = {Tags.AUTH},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "User registered successfully.",
                            content = @Content(schema = @Schema(implementation = AuthResponse.class)))})
    public AuthResponse verifyEmail(@RequestParam("token") @Valid String token, HttpServletResponse res) {
        return registrationService.verifyEmail(token, res);
    }

    @PostMapping("/verify/resend")
    @Operation(description = "Resends a verification email.",
            tags = {Tags.AUTH},
            responses = {
                    @ApiResponse(responseCode = HttpStatus.OK, description = "Verification mail resend successfully.",
                            content = @Content(schema = @Schema(implementation = StringResponse.class)))})
    public StringResponse resendVerificationEmail(@RequestBody @Valid @Email String email) {
        return new StringResponse(registrationService.resendVerificationEmail(email));
    }

}
