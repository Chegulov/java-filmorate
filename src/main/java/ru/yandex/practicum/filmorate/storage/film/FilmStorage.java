package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> getFilms();

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(int id);

    void addLike(int userId, int filmId);

    void removeLike(int userId, int filmId);

    void deleteFilmById(int id);

    List<Film> getRecommendationFilmForUser(int id);
}
