
package com.market.backend_market.magazynKlientow;

import com.market.backend_market.models.User;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositoryTest {

    @Autowired
    private User_Repo userRepository;

    // Przygotowanie przykładowego użytkownika
    private User createTestUser() {

        String unique = UUID.randomUUID().toString();

        User user = new User();

        user.setImie("Jan");
        user.setNazwisko("Kowalski");
        user.setLogin("test_" + unique);
        user.setHaslo("$2a$10$testowyHash");
        user.setMail("test_" + unique + "@example.com");

        return user;
    }

    // TEST 1 - dodawanie użytkownika
    @Test
    void shouldSaveUser() {

        User user = createTestUser();

        Long id = userRepository.save(user);

        assertNotNull(id);
        assertTrue(id > 0);
    }

    // TEST 2 - wyszukiwanie po ID
    @Test
    void shouldFindUserById() {

        User user = createTestUser();

        Long id = userRepository.save(user);

        Optional<User> result =
                userRepository.findById(id);

        assertTrue(result.isPresent());

        assertEquals(user.getLogin(),
                result.get().getLogin());

        assertEquals(user.getMail(),
                result.get().getMail());
    }

    // TEST 3 - wyszukiwanie po loginie
    @Test
    void shouldFindUserByLogin() {

        User user = createTestUser();

        userRepository.save(user);

        Optional<User> result =
                userRepository.findByLogin(user.getLogin());

        assertTrue(result.isPresent());

        assertEquals(user.getImie(),
                result.get().getImie());

        assertEquals(user.getNazwisko(),
                result.get().getNazwisko());
    }

    // TEST 4 - sprawdzanie istnienia loginu
    @Test
    void shouldCheckIfLoginExists() {

        User user = createTestUser();

        userRepository.save(user);

        assertTrue(
                userRepository.existsByLogin(user.getLogin())
        );

        assertFalse(
                userRepository.existsByLogin("nieistniejacy_login")
        );
    }

    // TEST 5 - wyszukiwanie nieistniejącego użytkownika
    @Test
    void shouldReturnEmptyWhenUserDoesNotExist() {

        Optional<User> result =
                userRepository.findByLogin("brak_takiego_uzytkownika");

        assertTrue(result.isEmpty());
    }
}
