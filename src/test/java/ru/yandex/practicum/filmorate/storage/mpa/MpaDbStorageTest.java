package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class MpaDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;
    private final Mpa mpa = new Mpa(3, "PG-13");
    private final List<Mpa> mpasTest = List.of(
            new Mpa(1, "G"),
            new Mpa(2, "PG"),
            new Mpa(3, "PG-13"),
            new Mpa(4, "R"),
            new Mpa(5, "NC-17")
    );

    @BeforeEach
    public void beforeEach() {
        mpaStorage = new MpaDbStorage(jdbcTemplate);
    }

    @Test
    public void shouldGetMpa() {
        Mpa savedMpa = mpaStorage.getMpa(3);

        assertThat(savedMpa)
                .isNotNull()
                .isEqualTo(mpa);
    }

    @Test
    public void shouldThrowExceptionWhenWrongId() {
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> mpaStorage.getMpa(9)
        );

        assertEquals(exception.getMessage(), "Рейтинга с id=9 нет");
    }

    @Test
    public void shouldGetMpas() {
        List<Mpa> savedMpas = mpaStorage.getMpas();

        assertThat(savedMpas)
                .isNotNull()
                .isEqualTo(mpasTest);
    }
}