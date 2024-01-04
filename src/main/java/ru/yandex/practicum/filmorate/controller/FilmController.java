package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
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
    public Film create(@Valid @RequestBody Film film) {
        filmService.getFilmStorage().create(film);

        log.info("фильм с id={} добавлен", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        filmService.getFilmStorage().update(film);

        return film;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmService.getFilmStorage().getFilmById(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable int id,
                        @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film removeLike(@PathVariable int id,
                           @PathVariable int userId) {
        return filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") int count,
                                      @RequestParam (defaultValue = "0") int genreId,
                                      @RequestParam (defaultValue = "0") int year) {
        return filmService.getPopularFilms(count, genreId, year);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmByDirectorSort(@PathVariable int directorId,
                                            @RequestParam String sortBy) {
        return filmService.getSortedFilmByDirector(directorId, sortBy);
    }

    @Valid
    @GetMapping("/search")
    public List<Film> searchFilms(@RequestParam String query,
                                  @RequestParam @NotNull List<String> by) {
        log.info("Request received: GET /films/search");
        List<Film> searchedFilms = filmService.getSearcherFilms(query, by);
        log.info("Request GET /films/search processed: searchedFilms: {}", searchedFilms);
        return searchedFilms;
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        filmService.getFilmStorage().deleteFilmById(id);
    }

    @GetMapping("/common")
    public List<Film> getFilmByCommonUserAndFriend(@RequestParam int userId,
                                                   @RequestParam int friendId) {
        return filmService.commonFilms(userId, friendId);
    }
}
