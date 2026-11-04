
package com.market.backend_market.uslugi;

import com.market.backend_market.magazynKlientow.User_Repo;
import com.market.backend_market.models.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private User_Repo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final String imie = "Jan";
    private final String nazwisko = "Kowalski";
    private final String login = "jan123";
    private final String haslo = "TajneHaslo123!";
    private final String email = "jan@test.pl";

    @Test
    void shouldRegisterUser() {

        when(userRepo.existsByLogin(login)).thenReturn(false);
        when(userRepo.existsByEmail(email)).thenReturn(false);

        when(passwordEncoder.encode(haslo))
                .thenReturn("$2a$12$zahashowaneHaslo");

        when(userRepo.save(any(User.class))).thenReturn(1L);

        Long id = userService.register(
                imie, nazwisko, login, haslo, email
        );

        assertEquals(1L, id);

        verify(userRepo).save(argThat(user ->
                user.getImie().equals(imie)
                        && user.getNazwisko().equals(nazwisko)
                        && user.getLogin().equals(login)
                        && user.getMail().equals(email)
                        && user.getHaslo().equals("$2a$12$zahashowaneHaslo")
        ));

        verify(passwordEncoder).encode(haslo);
    }

    @Test
    void shouldNotRegisterWhenLoginAlreadyExists() {

        when(userRepo.existsByLogin(login)).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(
                        imie, nazwisko, login, haslo, email
                )
        );

        verify(userRepo, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    void shouldNotRegisterWhenEmailAlreadyExists() {

        when(userRepo.existsByLogin(login)).thenReturn(false);
        when(userRepo.existsByEmail(email)).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> userService.register(
                        imie, nazwisko, login, haslo, email
                )
        );

        verify(userRepo, never()).save(any(User.class));
        verify(passwordEncoder, never()).encode(anyString());
    }
}
