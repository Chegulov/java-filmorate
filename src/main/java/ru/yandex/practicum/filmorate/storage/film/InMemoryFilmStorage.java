package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Qualifier("inMemoryFilmStorage")
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCount = 0;

    @Override
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        idCount++;
        film.setId(idCount);
        films.put(idCount, film);

        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.containsKey(film.getId())) {
            log.info("Фильм с id={} обновлён", film.getId());
            films.put(film.getId(), film);
        } else {
            String msg = String.format("Фильм с id=%d не найден", film.getId());
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);

        if (film == null) {
            String msg = String.format("Фильм с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        return film;
    }

    @Override
    public void addLike(int userId, int filmId) {

    }

    @Override
    public void removeLike(int userId, int filmId) {

    }

    @Override
    public void deleteFilmById(int id) {

    }
}
