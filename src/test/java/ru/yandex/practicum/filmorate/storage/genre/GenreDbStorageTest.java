package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class GenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;

    private final Genre genre = new Genre(5, "Документальный");

    private final List<Genre> genresTest = List.of(
            new Genre(1, "Комедия"),
            new Genre(2, "Драма"),
            new Genre(3, "Мультфильм"),
            new Genre(4, "Триллер"),
            new Genre(5, "Документальный"),
            new Genre(6, "Боевик")
    );

    @BeforeEach
    public void beforeEach() {
        genreStorage =  new GenreDbStorage(jdbcTemplate);
    }

    @Test
    public void shouldGetGenre() {
        Genre savedGenre = genreStorage.getGenre(5);

        assertThat(savedGenre)
                .isNotNull()
                .isEqualTo(genre);
    }

    @Test
    public void shouldThrowExceptionWhenWrongId() {
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> genreStorage.getGenre(15)
        );

        assertEquals(exception.getMessage(), "Жанра с id=15 нет");
    }

    @Test
    public void shouldGetGenres() {
        List<Genre> savedGenres = genreStorage.getGenres().stream().sorted(Comparator.comparingInt(Genre::getId)).collect(Collectors.toList());

        assertThat(savedGenres)
                .isNotNull()
                .isEqualTo(genresTest);
    }
}