package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getFilms() {
        return filmService.getFilmStorage().getFilms();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        validate(film);
        filmService.getFilmStorage().create(film);

        log.info("фильм с id={} добавлен", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        validate(film);
        filmService.getFilmStorage().update(film);

        return film;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmStorage().getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getPopularFilms(count);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        filmService.getFilmStorage().deleteFilmById(id);
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
