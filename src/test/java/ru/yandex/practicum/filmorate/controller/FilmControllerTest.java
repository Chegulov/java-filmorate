package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {

    private FilmController filmController;

    @BeforeEach
    public void init() {
        filmController = new FilmController(new FilmService(new InMemoryFilmStorage(), new InMemoryUserStorage()));
    }

    @Test
    void shouldThrowExceptionIfEmptyName() {
        Film film = Film.builder()
                .name("")
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(exception.getMessage(), "Имя фильма не может быть пустым.");
    }

    @Test
    void shouldThrowExceptionIfDescLenghtMoreThan200() {
        String description = "я".repeat(201);
        Film film = Film.builder()
                .name("Терминатор")
                .description(description)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(exception.getMessage(), "Длинна описания не должна превышать 200 символов.");
    }

    @Test
    void shouldThrowExceptionIfEarlyDate() {
        Film film = Film.builder()
                .name("Терминатор")
                .description("И восстали машины из пепла ядерного огня")
                .releaseDate(LocalDate.of(1895, 12, 27))
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(exception.getMessage(), "Дата релиза не может быть раньше чем 28.12.1895.");
    }

    @Test
    void shouldThrowExceptionIfDurationNotPositive() {
        Film film = Film.builder()
                .name("Терминатор")
                .description("И восстали машины из пепла ядерного огня")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(0)
                .build();

        final ValidationException exception = assertThrows(
                ValidationException.class,
                () -> filmController.create(film)
        );

        assertEquals(exception.getMessage(), "Продолжительность фильма должна быть положительной");
    }

    @Test
    void shouldCreateFilm() {
        Film film = Film.builder()
                .name("Терминатор")
                .description("И восстали машины из пепла ядерного огня")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(107)
                .build();
        filmController.create(film);

        assertNotNull(filmController.getFilms().get(0));
        assertEquals(film, filmController.getFilms().get(0));
    }

    @Test
    void shouldUpdateFilm() {
        Film film = Film.builder()
                .name("Терминатор")
                .description("И восстали машины из пепла ядерного огня")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(107)
                .build();
        filmController.create(film);

        Film film2 = Film.builder()
                .name("Терминатор 2")
                .description("И восстали машины из пепла ядерного огня опять")
                .releaseDate(LocalDate.of(1991, 12, 25))
                .duration(137)
                .build();
        film2.setId(1);
        filmController.update(film2);

        assertNotNull(filmController.getFilms().get(0));
        assertEquals(film2, filmController.getFilms().get(0));
    }
}
