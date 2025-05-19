package com.sep.backend.auth.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Sends an OTP to the email.
     *
     * @param email The email.
     * @param otp   The OTP.
     */
    public void sendOtp(String email, String otp) {
        log.debug("Sending OTP to {}", email);
        sendEmail(email, "SEP-Drive: OTP Code", "Dein OTP-Code ist: " + otp);
        log.info("Sent OTP to {}", email);
    }

    /**
     * Sends a verification link to the email address of the recipient.
     *
     * @param to   The recipient email address.
     * @param link The verification link.
     */
    public void sendVerificationLink(String to, String link) {
        log.debug("Sending verification link to {}", to);
        sendEmail(to, "SEP-Drive: Verifizierungs-Link", "Dein Verifizierungs-Link ist: " + link);
        log.info("Sent verification link to {}", to);
    }

    /**
     * Sends an email to the specified email address.
     *
     * @param to      The recipient email address.
     * @param subject The subject of the email.
     * @param text    The text of the email.
     */
    private void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }
}
