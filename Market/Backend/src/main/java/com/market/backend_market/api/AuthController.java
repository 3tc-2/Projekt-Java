
package com.market.backend_market.api;

import com.market.backend_market.magazynKlientow.User_Repo;
import com.market.backend_market.models.User;
import com.market.backend_market.uslugi.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final User_Repo userRepo;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    public AuthController(
            UserService userService,
            User_Repo userRepo,
            AuthenticationManager authenticationManager,
            SecurityContextRepository securityContextRepository
    ) {
        this.userService = userService;
        this.userRepo = userRepo;
        this.authenticationManager = authenticationManager;
        this.securityContextRepository = securityContextRepository;
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
        if (request.login() == null || request.login().isBlank()
                || request.haslo() == null || request.haslo().isBlank()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Podaj login i hasło"));
        }

        try {
            Authentication authentication =
                    authenticationManager.authenticate(
                            UsernamePasswordAuthenticationToken
                                    .unauthenticated(
                                            request.login(),
                                            request.haslo()
                                    )
                    );

            // Zmiana identyfikatora sesji po zalogowaniu
            if (httpRequest.getSession(false) != null) {
                httpRequest.changeSessionId();
            } else {
                httpRequest.getSession(true);
            }

            SecurityContext context =
                    SecurityContextHolder.createEmptyContext();

            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);

            securityContextRepository.saveContext(
                    context,
                    httpRequest,
                    httpResponse
            );

            User user = userRepo
                    .findByLogin(authentication.getName())
                    .orElseThrow();

            return ResponseEntity.ok(Map.of(
                    "message", "Zalogowano pomyślnie",
                    "userId", user.getId(),
                    "login", user.getLogin()
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "error", "Nieprawidłowy login lub hasło"
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
}
