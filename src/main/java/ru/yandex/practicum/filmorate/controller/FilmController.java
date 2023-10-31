package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {
    private final static Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCount = 0;

    @GetMapping("/films")
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping(value = "/films")
    public Film create(@RequestBody Film film) {
        validate(film);

        idCount++;
        film.setId(idCount);
        films.put(idCount, film);

        log.info("фильм с id={} добавлен", film.getId());
        return film;
    }

    @PutMapping("/films")
    public ResponseEntity<Film> update(@RequestBody Film film) {
        validate(film);
        if (films.containsKey(film.getId())) {
            log.info("фильм с id={} обновлён", film.getId());
            films.put(film.getId(), film);
            return new ResponseEntity<>(film, HttpStatus.OK);
        } else {
            log.info("фильм с id={} не найден", film.getId());
            return new ResponseEntity<>(film, HttpStatus.NOT_FOUND);
        }
    }

    private void validate(Film film) {
        String msg;
        if (film.getName() == null || film.getName().isBlank()) {
            msg = "Имя фильма не может быть пустым.";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (film.getDescription().length() > 200) {
            msg = "Длинна описания не должна превышать 200 символов.";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            msg = "Дата релиза не может быть раньше чем 28.12.1895.";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (film.getDuration() <= 0) {
            msg = "Продолжительность фильма должна быть положительной";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }
}
