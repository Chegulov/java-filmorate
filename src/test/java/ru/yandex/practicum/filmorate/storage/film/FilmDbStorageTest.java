package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private Film film;
    private Film film2;
    private User user;

    @BeforeEach
    public void beforeEach() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
        film = Film.builder()
                .name("Терминатор")
                .description("И восстали машины из пепла ядерного огня")
                .releaseDate(LocalDate.of(1984, 10, 26))
                .duration(107)
                .mpa(new Mpa(1, "G"))
                .build();
        film2 = Film.builder()
                .name("Терминатор 2")
                .description("И восстали машины из пепла ядерного огня опять")
                .releaseDate(LocalDate.of(1991, 12, 25))
                .duration(137)
                .mpa(new Mpa(2, "PG"))
                .build();
        user = User.builder()
                .email("user@films.ru")
                .login("tada")
                .birthday(LocalDate.of(1990, 12, 5))
                .name("Сергей")
                .build();
    }

    @Test
    public void shouldAddFindFilm() {

        filmStorage.create(film);
        Film savedFilm = filmStorage.getFilmById(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film);
    }

    @Test
    public void shouldThrowExceptionWhenWrongId() {
        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> filmStorage.getFilmById(1)
        );

        assertEquals(exception.getMessage(), "Фильм с id=1 не найден");
    }

    @Test
    public void shouldUpdateFilm() {
        film2.setId(1);
        filmStorage.create(film);
        filmStorage.update(film2);
        Film savedFilm = filmStorage.getFilmById(1);

        assertThat(savedFilm)
                .isNotNull()
                .usingRecursiveComparison()
                .isEqualTo(film2);
    }

    @Test
    public void shouldNotUpdateAndThrowExceptionWhenWrongId() {
        film2.setId(2);
        filmStorage.create(film);

        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> filmStorage.update(film2)
        );

        assertEquals(exception.getMessage(), "Фильм с id=2 не найден");
    }

    @Test
    public void shouldAddLike() {
        filmStorage.create(film);
        userStorage.create(user);
        filmStorage.addLike(1,1);

        Film savedFilm = filmStorage.getFilmById(1);

        assertNotNull(savedFilm);
        assertThat(savedFilm.getLikes())
                .isNotNull()
                .isEqualTo(Set.of(1));
    }

    @Test
    public void shouldRemoveLike() {
        filmStorage.create(film);
        userStorage.create(user);
        filmStorage.addLike(1,1);
        filmStorage.removeLike(1, 1);

        Film savedFilm = filmStorage.getFilmById(1);

        assertNotNull(savedFilm);
        assertTrue(savedFilm.getLikes().isEmpty());
    }

    @Test
    public void shouldThrowExceptionWhenRemoveIfNotLiked() {
        filmStorage.create(film);
        userStorage.create(user);

        final DataNotFoundException exception = assertThrows(
                DataNotFoundException.class,
                () -> filmStorage.removeLike(1, 1)
        );

        assertEquals(exception.getMessage(), "Лайка от пользователя с id=1 у фильма с id=1 нет");
    }

    @Test
    public void shouldGetFilms() {
        filmStorage.create(film);
        filmStorage.create(film2);

        List<Film> savedFilms = filmStorage.getFilms();

        assertThat(savedFilms)
                .isNotNull()
                .isEqualTo(List.of(film, film2));
    }
}