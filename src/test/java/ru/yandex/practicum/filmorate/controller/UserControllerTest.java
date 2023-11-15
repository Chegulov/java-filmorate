package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class UserControllerTest {

    private UserController userController;

    @BeforeEach
    public void init() {
        userController = new UserController(new UserService(new InMemoryUserStorage()));
    }

    @Test
    void shouldThrowExceptionIfEmptyEmail() {
        User user = User.builder()
                .email("")
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(exception.getMessage(), "Почта не может быть пустой");
    }

    @Test
    void shouldThrowExceptionIfWrongEmail() {
        User user = User.builder()
                .email("1231245")
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(exception.getMessage(), "Почта должна содержать \"@\"");
    }

    @Test
    void shouldThrowExceptionIfEmptyLogin() {
        User user = User.builder()
                .email("123@321.com")
                .login("")
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(exception.getMessage(), "Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldThrowExceptionIfLoginWithSpaces() {
        User user = User.builder()
                .email("123@321.com")
                .login("123 123")
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(exception.getMessage(), "Логин не может быть пустым и содержать пробелы");
    }

    @Test
    void shouldThrowExceptionIfBirthdayInFuture() {
        User user = User.builder()
                .email("123@321.com")
                .login("123123")
                .birthday(LocalDate.of(2077, 1, 1))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userController.create(user)
        );

        assertEquals(exception.getMessage(), "Дата рождения не может быть в будущем.");
    }

    @Test
    void shouldCreateUser() {
        User user = User.builder()
                .email("123@321.com")
                .login("123123")
                .birthday(LocalDate.of(2001, 1, 1))
                .name("Jonn")
                .build();
        userController.create(user);

        assertNotNull(userController.getUsers().get(0));
        assertEquals(user, userController.getUsers().get(0));
    }

    @Test
    void shouldUpdateUser() {
        User user = User.builder()
                .email("123@321.com")
                .login("123123")
                .birthday(LocalDate.of(2001, 1, 1))
                .name("Jonn")
                .build();
        userController.create(user);

        User user2 = User.builder()
                .email("666@321.com")
                .login("newlogin")
                .birthday(LocalDate.of(2001, 1, 1))
                .name("JonnNewman")
                .build();
        user2.setId(1);
        userController.update(user2);

        assertNotNull(userController.getUsers().get(0));
        assertEquals(user2, userController.getUsers().get(0));
    }

    @Test
    void shouldUseLoginIfEmptyName() {
        User user = User.builder()
                .email("123@321.com")
                .login("123123")
                .birthday(LocalDate.of(2001, 1, 1))
                .name("")
                .build();
        userController.create(user);

        assertEquals(user.getName(), "123123");
    }
}
