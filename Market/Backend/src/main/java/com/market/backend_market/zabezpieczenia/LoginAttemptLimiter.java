
package com.market.backend_market.zabezpieczenia;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginAttemptLimiter {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration BLOCK_DURATION =
            Duration.ofMinutes(5);

    private final ConcurrentHashMap<String, Attempt> attempts =
            new ConcurrentHashMap<>();

    private record Attempt(
            int failures,
            Instant blockedUntil
    ) {}

    // Sprawdzenie, czy adres IP jest zablokowany
    public boolean isBlocked(String ip) {
        Attempt attempt = attempts.get(ip);

        if (attempt == null) {
            return false;
        }

        if (attempt.blockedUntil() != null) {
            if (Instant.now().isBefore(attempt.blockedUntil())) {
                return true;
            }

            attempts.remove(ip, attempt);
        }

        return false;
    }

    // Zapisanie nieudanej próby logowania
    public void loginFailed(String ip) {
        Instant now = Instant.now();

        attempts.compute(ip, (key, previous) -> {

            if (previous == null) {
                return new Attempt(1, null);
            }

            if (previous.blockedUntil() != null) {
                if (now.isBefore(previous.blockedUntil())) {
                    return previous;
                }

                return new Attempt(1, null);
            }

            int failures = previous.failures() + 1;

            if (failures >= MAX_ATTEMPTS) {
                return new Attempt(
                        failures,
                        now.plus(BLOCK_DURATION)
                );
            }

            return new Attempt(failures, null);
        });
    }

    // Usunięcie licznika po poprawnym logowaniu
    public void loginSucceeded(String ip) {
        attempts.remove(ip);
    }

    // Liczba sekund pozostałych do odblokowania
    public long retryAfterSeconds(String ip) {
        Attempt attempt = attempts.get(ip);

        if (attempt == null || attempt.blockedUntil() == null) {
            return 0;
        }

        long seconds = Duration.between(
                Instant.now(),
                attempt.blockedUntil()
        ).toSeconds();

        return Math.max(1, seconds);
    }
}
