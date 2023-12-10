package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserDbStorage userStorage;
    private User user;
    private User user2;

    @BeforeEach
    public void beforeEach() {
        userStorage = new UserDbStorage(jdbcTemplate);
        user =  User.builder()
                .email("123@321.com")
                .login("123123")
                .birthday(LocalDate.of(2001, 1, 1))
                .name("Jonn")
                .build();
        user2 = User.builder()
                .email("666@321.com")
                .login("newlogin")
                .birthday(LocalDate.of(2011, 1, 1))
                .name("JonnNewman")
                .build();
    }


}