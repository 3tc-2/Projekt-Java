
package com.market.backend_market.api;

import com.market.backend_market.magazynKlientow.User_Repo;
import com.market.backend_market.models.User;
import com.market.backend_market.uslugi.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.market.backend_market.uslugi.PasswordResetService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;
import com.market.backend_market.zabezpieczenia.LoginAttemptLimiter;


import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final User_Repo userRepo;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final LoginAttemptLimiter loginAttemptLimiter;
    private final PasswordResetService passwordResetService;


    public AuthController(
            UserService userService,
            User_Repo userRepo,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository,
            LoginAttemptLimiter loginAttemptLimiter,
            PasswordResetService passwordResetService
    ) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
        this.loginAttemptLimiter = loginAttemptLimiter;
        this.passwordResetService = passwordResetService;
    }


    // Pobieranie tokenu CSRF
    @GetMapping("/csrf")
    public Map<String, String> csrf(CsrfToken csrfToken) {
        return Map.of(
                "headerName", csrfToken.getHeaderName(),
                "token", csrfToken.getToken()
        );
    }

    // Rejestracja
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest request
    ) {
        System.out.println(">>> WYWOŁANO REJESTRACJĘ <<<");
        try {
            Long userId = userService.register(
                    request.imie(),
                    request.nazwisko(),
                    request.login(),
                    request.haslo(),
                    request.email()
            );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Użytkownik został zarejestrowany",
                            "userId", userId
                    ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Logowanie

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        String ip = httpRequest.getRemoteAddr();

        // 1. Sprawdzenie blokady IP
        if (loginAttemptLimiter.isBlocked(ip)) {
            long retryAfter =
                    loginAttemptLimiter.retryAfterSeconds(ip);

            return ResponseEntity.status(429)
                    .header("Retry-After", String.valueOf(retryAfter))
                    .body(Map.of(
                            "error",
                            "Zbyt wiele prób logowania. Spróbuj ponownie później."
                    ));
        }

        // 2. Walidacja danych
        if (request == null
                || request.login() == null
                || request.login().isBlank()
                || request.haslo() == null
                || request.haslo().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", "Podaj login i hasło"
                    ));
        }

        try {
            // 3. Uwierzytelnienie użytkownika
            Authentication authentication =
                    authenticationManager.authenticate(
                            UsernamePasswordAuthenticationToken
                                    .unauthenticated(
                                            request.login(),
                                            request.haslo()
                                    )
                    );

            // 4. Zabezpieczenie przed przejęciem sesji
            if (httpRequest.getSession(false) != null) {
                httpRequest.changeSessionId();
            } else {
                httpRequest.getSession(true);
            }

            // 5. Zapis uwierzytelnienia w sesji
            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(
                    context,
                    httpRequest,
                    httpResponse
            );

            // 6. Pobranie danych zalogowanego użytkownika
            User user = userRepo
                    .findByLogin(authentication.getName())
                    .orElseThrow();

            // 7. Wyzerowanie licznika błędów
            loginAttemptLimiter.loginSucceeded(ip);

            return ResponseEntity.ok(Map.of(
                    "message", "Zalogowano pomyślnie",
                    "userId", user.getId(),
                    "login", user.getLogin()
            ));

        } catch (AuthenticationException e) {

            // 8. Zapisanie nieudanej próby
            loginAttemptLimiter.loginFailed(ip);

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error",
                            "Nieprawidłowy login lub hasło"
                    ));
        }
    }


    // Dane zalogowanego użytkownika
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        User user = userRepo
                .findByLogin(authentication.getName())
                .orElseThrow();

        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "login", user.getLogin(),
                "imie", user.getImie(),
                "nazwisko", user.getNazwisko(),
                "email", user.getMail()
        ));
    }


    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication,
            HttpServletRequest httpRequest
    ) {

        try {
            // 1. Login pobieramy z zalogowanej sesji
            String login = authentication.getName();

            // 2. Zmiana hasła w bazie danych
            userService.changePassword(
                    login,
                    request.oldPassword(),
                    request.newPassword()
            );

            // 3. Unieważnienie obecnej sesji
            var session = httpRequest.getSession(false);

            if (session != null) {
                session.invalidate();
            }

            SecurityContextHolder.clearContext();

            // 4. Odpowiedź
            return ResponseEntity.ok(Map.of(
                    "message",
                    "Hasło zostało zmienione. Zaloguj się ponownie."
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "error", e.getMessage()
                    ));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(
            @RequestBody ForgotPasswordRequest request
    ) {
        passwordResetService.forgotPassword(request.email());

        return ResponseEntity.ok(Map.of(
                "message",
                "Jeśli konto istnieje, wysłaliśmy instrukcję resetowania hasła."
        ));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody ResetPasswordRequest request
    ) {
        try {
            passwordResetService.resetPassword(
                    request.token(),
                    request.newPassword()
            );

            return ResponseEntity.ok(Map.of(
                    "message",
                    "Hasło zostało zmienione. Możesz się zalogować."
            ));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        }
    }


}
