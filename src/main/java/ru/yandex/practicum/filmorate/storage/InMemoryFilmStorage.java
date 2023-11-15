package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
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
            throw new NotFoundException(msg);
        }

        return film;
    }

    @Override
    public Film getFilmById(int id) {
        Film film = films.get(id);

        if (film == null) {
            String msg = String.format("Фильм с id=%d не найден", id);
            log.info(msg);
            throw new NotFoundException(msg);
        }

        return film;
    }
}
