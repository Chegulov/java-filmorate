package ru.yandex.practicum.filmorate.storage.director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    public DirectorDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Director> getDirectors() {
        return jdbcTemplate.query("SELECT * FROM DIRECTORS", this::makeDirector);
    }

    @Override
    public Director create(Director director) {

        if (director.getName().isBlank()) {
            throw new ValidationException("Имя режиссёра  не может быть пустым");
        } else {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement stmt = connection.prepareStatement("INSERT INTO DIRECTORS(DIRECTOR_NAME) VALUES ( ?)",
                        new String[]{"DIRECTOR_ID"});
                stmt.setString(1, director.getName());
                return stmt;
            }, keyHolder);
            director.setId(Objects.requireNonNull(keyHolder.getKey()).longValue());
            return director;
        }

    }

    @Override
    public Director update(Director director) {
        if (jdbcTemplate.update("UPDATE DIRECTORS SET DIRECTOR_NAME = ? WHERE DIRECTOR_ID = ?",
                director.getName(), director.getId()) > 0) {
            return director;
        } else {
            throw new DataNotFoundException(String.format("Режиссера с id = %d не существует",
                    director.getId()));
        }
    }

    @Override
    public Director getDirectorById(int id) {
        String sqlQuery = "SELECT * FROM DIRECTORS WHERE DIRECTOR_ID = ?";
        Director director;
        try {
            director = jdbcTemplate.queryForObject(sqlQuery, this::makeDirector, id);
        } catch (EmptyResultDataAccessException e) {
            String msg = String.format("Режиссера с id=%d нет", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        return director;
    }

    @Override
    public void deleteDirectorById(int id) {
        jdbcTemplate.update("DELETE  FROM DIRECTORS WHERE DIRECTOR_ID = ?", id);
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder()
                .id(rs.getLong("DIRECTOR_ID"))
                .name(rs.getString("DIRECTOR_NAME"))
                .build();
    }
}
