package com.sep.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "otp_verification_token")
@Entity
public class OtpVerificationTokenEntity extends AbstractEntity {

    // no relation to prevent joins
    @Email
    @NotBlank
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(name = "otp", unique = true, nullable = false)
    private String otp;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    public OtpVerificationTokenEntity(String email) {
        this.email = email;
    }
}
