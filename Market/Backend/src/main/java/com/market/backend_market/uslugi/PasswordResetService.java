
package com.market.backend_market.uslugi;

import com.market.backend_market.magazynKlientow.PasswordReset_Repo;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
public class PasswordResetService {

    private final PasswordReset_Repo repository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    private final SecureRandom secureRandom = new SecureRandom();

    public PasswordResetService(
            PasswordReset_Repo repository,
            PasswordEncoder passwordEncoder,
            EmailService emailService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    // Wyslanie linku resetowania
    public void forgotPassword(String email) {

        if (email == null || email.isBlank()
                || email.length() > 254) {
            return;
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        repository.findUserIdByEmail(normalizedEmail)
                .ifPresent(userId -> {

                    byte[] randomBytes = new byte[32];
                    secureRandom.nextBytes(randomBytes);

                    String token = Base64.getUrlEncoder()
                            .withoutPadding()
                            .encodeToString(randomBytes);

                    String tokenHash = hashToken(token);

                    repository.saveToken(userId, tokenHash);

                    emailService.sendPasswordResetEmail(
                            normalizedEmail,
                            token
                    );
                });
    }

    // Ustawienie nowego hasla
    @Transactional
    public void resetPassword(
            String token,
            String newPassword
    ) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "Brak tokenu resetowania"
            );
        }

        if (newPassword == null
                || newPassword.length() < 8
                || newPassword.getBytes(
                StandardCharsets.UTF_8
        ).length > 72) {

            throw new IllegalArgumentException(
                    "Hasło musi mieć minimum 8 znaków " +
                            "i maksymalnie 72 bajty UTF-8"
            );
        }

        String tokenHash = hashToken(token);

        Long userId = repository
                .findValidTokenForUpdate(tokenHash)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Token jest nieprawidłowy lub wygasł"
                        )
                );

        String passwordHash =
                passwordEncoder.encode(newPassword);

        repository.updatePassword(userId, passwordHash);

        // Uniewazniamy wszystkie tokeny tego uzytkownika
        repository.invalidateUserTokens(userId);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] result = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(result);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "SHA-256 jest niedostępny", e
            );
        }
    }
}
