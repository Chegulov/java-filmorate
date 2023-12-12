package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getGenre(int id) {
        String sqlQuery = "SELECT * FROM genre WHERE genre_id=?";
        Genre genre;
        try {
            genre = jdbcTemplate.queryForObject(sqlQuery, this::makeGenre, id);
        } catch (EmptyResultDataAccessException e) {
            String msg = String.format("Жанра с id=%d нет", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        return genre;
    }

    @Override
    public List<Genre> getGenres() {
        String sqlQuery = "SELECT * FROM genre";
        return jdbcTemplate.query(sqlQuery,this::makeGenre).stream()
                .sorted(Comparator.comparingInt(Genre::getId))
                .collect(Collectors.toList());
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
