package com.sep.backend;

public class ErrorMessages {
    public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token.";

    public static final String INVALID_ACCESS_TOKEN = "Invalid access token.";

    public static final String INVALID_USERNAME_CHARACTER_AT = "Username cannot contain @ as character.";

    public static final String PENDING_VERIFICATION = "Pending verification.";

    public static final String INVALID_ROLE = "Invalid role.";

    public static final String INVALID_OTP = "OTP invalid.";

    public static final String INVALID_CREDENTIALS = "Invalid credentials.";

    public static final String INVALID_VERIFICATION_TOKEN = "Invalid verification token.";

    public static final String INVALID_PROFILE_PICTURE_FORMAT = "Invalid profile picture format.";


    public static final String NOT_FOUND_CUSTOMER = "Customer not found.";

    public static final String NOT_FOUND_DRIVER = "Driver not found.";

    public static final String NOT_FOUND_USER = "User not found.";

    public static final String NOT_FOUND_OTP = "OTP not found.";


    public static final String ALREADY_EXISTS_USERNAME = "Username already exists.";

    public static final String ALREADY_EXISTS_EMAIL = "Email already exists.";


    public static final String EXPIRED_OTP = "OTP expired.";
    public static final String INVALID_CAR_TYPE = "Invalid car type";

    public ErrorMessages() {
        throw new UnsupportedOperationException("Cannot instantiate ErrorMessages.");
    }
}
