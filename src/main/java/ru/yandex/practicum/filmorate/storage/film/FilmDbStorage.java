package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.DuplicateDataException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getFilms() {
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM films AS f JOIN mpa ON f.mpa_id=mpa.mpa_id";
        return jdbcTemplate.query(sqlQuery, this::makeFilm);
    }

    @Override
    public Film create(Film film) {
        int filmId = addFilmToDb(film);
        film.setId(filmId);
        String sqlQuery = "INSERT into film_genre (film_id, genre_id) VALUES (?, ?)";
        if (!film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(sqlQuery, filmId, genre.getId());
            }
        }
        if (!film.getDirectors().isEmpty()) {
            for (Director director : film.getDirectors()) {
                jdbcTemplate.update("INSERT into FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) " +
                        "VALUES (?, ?)", filmId, director.getId());
            }
        }
        return film;
    }

    @Override
    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET " +
                "name=?, description=?, release_date=?, duration=?, mpa_id=? WHERE film_id=?";
        if (jdbcTemplate.update(
                sqlQuery,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()) == 0
        ) {
            String msg = String.format("Фильм с id=%d не найден", film.getId());
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        String sqlQ = "DELETE FROM film_genre WHERE film_id=?";
        jdbcTemplate.update(sqlQ, film.getId());

        String sqlDirector = "DELETE FROM FILM_DIRECTOR WHERE FILM_ID=?";
        jdbcTemplate.update(sqlDirector, film.getId());

        if (film.getGenres() != null && film.getGenres().size() != 0) {
            String sqlQ2 = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            film.getGenres().forEach(genre -> jdbcTemplate.update(sqlQ2, film.getId(), genre.getId()));
        }
        if (film.getDirectors() != null && film.getDirectors().size() != 0) {
            String sqlQ3 = "INSERT INTO FILM_DIRECTOR (FILM_ID, DIRECTOR_ID) VALUES (?, ?)";
            film.getDirectors().forEach(director -> jdbcTemplate.update(sqlQ3, film.getId(), director.getId()));
        }
        return film;
    }

    @Override
    public Film getFilmById(int id) {
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM films AS f JOIN mpa ON f.mpa_id=mpa.mpa_id WHERE f.film_id=?";
        Film film;
        try {
            film = jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, id);
        } catch (EmptyResultDataAccessException e) {
            String msg = String.format("Фильм с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        return film;
    }

    @Override
    public void addLike(int userId, int filmId) {
        if (dbContainsFilm(filmId)) {
            String msg = String.format("Фильм с id=%d не найден", filmId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        String sqlQuery = "INSERT INTO likes (user_id, film_id) VALUES (?, ?)";
        try {
            jdbcTemplate.update(sqlQuery, userId, filmId);
        } catch (DuplicateKeyException e) {
            String msg = "Одному фильму нельзя поставить лайк дважды";
            log.info(msg);
            throw new DuplicateDataException(msg);
        }
    }

    @Override
    public void removeLike(int userId, int filmId) {
        if (dbContainsFilm(filmId)) {
            String msg = String.format("Фильм с id=%d не найден", filmId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        String sqlQuery = "DELETE FROM likes where user_id=? AND film_id=?";
        if (jdbcTemplate.update(sqlQuery, userId, filmId) == 0) {
            String msg = String.format("Лайка от пользователя с id=%d у фильма с id=%d нет", userId, filmId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
    }

    @Override
    public void deleteFilmById(int filmId) {
        if (dbContainsFilm(filmId)) {
            throw new DataNotFoundException(String.format("Фильм с id = %d не найден", filmId));
        } else {
            jdbcTemplate.update("DELETE  FROM FILMS WHERE FILM_ID = ?", filmId);
            log.info("Фильм с id {} удален", filmId);
        }

    }

    public List<Film> getRecommendationFilmForUser(int id) {
        String sqlQuery = "SELECT F.FILM_ID, NAME, DESCRIPTION, RELEASE_DATE, DURATION, " +
                "M.MPA_ID, MPA_NAME " +
                "FROM LIKES L " +
                "JOIN FILMS F on F.FILM_ID = L.FILM_ID " +
                "JOIN MPA M on M.MPA_ID = F.MPA_ID " +
                "LEFT JOIN (SELECT FILM_ID FROM LIKES WHERE USER_ID = ?) L11 ON L11.FILM_ID = L.FILM_ID " +
                "WHERE L.USER_ID = (SELECT L2.USER_ID FROM LIKES L1 " +
                "INNER JOIN LIKES L2 ON L1.USER_ID != L2.USER_ID " +
                "WHERE L1.USER_ID = ? " +
                "GROUP BY L2.USER_ID " +
                "ORDER BY COUNT(*) DESC LIMIT 1) " +
                "AND L11.FILM_ID IS NULL ";
        return jdbcTemplate.query(sqlQuery, this::makeFilm, id, id);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Film film = Film.builder()
                .id(rs.getInt("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .build();

        String sqlQuery = "SELECT user_id FROM likes WHERE film_id=?";
        film.getLikes().addAll(jdbcTemplate.query(
                        sqlQuery,
                        (rs1, rowNum1) -> rs1.getInt("user_id"),
                        film.getId()
                )
        );
        film.getGenres().addAll(findGenreById(film.getId()));
        film.getDirectors().addAll(findDirectorId(film.getId()));
        return film;
    }

    private int addFilmToDb(Film film) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("films")
                .usingGeneratedKeyColumns("film_id");
        return simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
    }

    private boolean dbContainsFilm(int filmId) {
        String sqlQuery = "SELECT f.*, mpa.mpa_name FROM films AS f JOIN mpa ON f.mpa_id=mpa.mpa_id WHERE f.film_id=?";
        try {
            jdbcTemplate.queryForObject(sqlQuery, this::makeFilm, filmId);
            return false;
        } catch (EmptyResultDataAccessException e) {
            return true;
        }
    }

    private Set<Genre> findGenreById(int id) {
        String sqlQuery = "SELECT g.genre_id, g.genre_name FROM film_genre AS fg " +
                "JOIN genre AS g ON fg.genre_id=g.genre_id WHERE fg.film_id=?";
        return new TreeSet<>(jdbcTemplate.query(sqlQuery, this::makeGenre, id));
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }

    private Set<Director> findDirectorId(int id) {
        return new TreeSet<>(jdbcTemplate.query("SELECT * FROM FILM_DIRECTOR fd " +
                "JOIN DIRECTORS D on D.DIRECTOR_ID = fd.DIRECTOR_ID " +
                "WHERE fd.FILM_ID = ?", this::makeDirector, id));
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }
}
