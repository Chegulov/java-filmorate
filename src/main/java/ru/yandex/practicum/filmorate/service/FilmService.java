package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorStorage directorStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       DirectorStorage directorStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.directorStorage = directorStorage;
    }

    public FilmStorage getFilmStorage() {
        return filmStorage;
    }

    public Film addLike(int id, int userId) {
        userStorage.getUserById(userId);
        filmStorage.addLike(userId, id);

        return filmStorage.getFilmById(id);
    }

    public Film removeLike(int id, int userId) {
        userStorage.getUserById(userId);

        if (!filmStorage.getFilmById(id).getLikes().contains(userId)) {
            String msg = String.format("У фильма с id=%d нет лайка от пользователя с id=%d", id, userId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }
        filmStorage.removeLike(userId, id);

        return filmStorage.getFilmById(id);
    }

    public List<Film> getPopularFilms(int count) {
        return filmStorage.getFilms().stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }

    public List<Film> commonFilms(int userId, int friendId) {
        List<Film> resultCommon = new ArrayList<>();
        filmStorage.getFilms().forEach(film -> {

                    if (filmStorage.getFilmById(film.getId()).getLikes().contains(userId)
                            && filmStorage.getFilmById(film.getId()).getLikes().contains(friendId))
                        resultCommon.add(film);

                    else {
                        log.info("Нет общих фильмов");
                    }
                }
        );

        return resultCommon;
    }

    public List<Film> getSortedFilmByDirector(int directorId, String sort) {
        Director director = directorStorage.getDirectorById(directorId);
        if (sort.equals("year")) {
            return filmStorage.getFilms().stream()
                    .filter(f -> f.getDirectors().contains(director))
                    .sorted((o1, o2) -> o1.getReleaseDate().compareTo(o2.getReleaseDate()))
                    .collect(Collectors.toList());
        }
        return filmStorage.getFilms().stream()
                .filter(f -> f.getDirectors().contains(director))
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .collect(Collectors.toList());
    }

    public List<Film> getSearcherFilms(String query, List<String> by) {
        List<Film> filmList = filmStorage.getFilms();
        Set<Film> searchedFilms = new HashSet<>();
        String lowQuery = query.toLowerCase();
        for (Film film : filmList) {
            if (by.contains("title") && by.contains("director")) {
                if (film.getName().toLowerCase().contains(lowQuery)) {
                    searchedFilms.add(film);
                }
                film.getDirectorsName().stream()
                        .filter(directorName -> directorName.toLowerCase().contains(lowQuery))
                        .map(directorName -> film)
                        .forEachOrdered(searchedFilms::add);
            } else if (by.contains("director")) {
                film.getDirectorsName().stream()
                        .filter(directorName -> directorName.toLowerCase().contains(lowQuery))
                        .map(directorName -> film)
                        .forEachOrdered(searchedFilms::add);
            } else if (by.contains("title")) {
                if (film.getName().toLowerCase().contains(lowQuery)) {
                    searchedFilms.add(film);
                }
            } else {
                throw new ValidationException("Передан некорректный запрос");
            }
        }
        return searchedFilms.stream()
                .sorted((o1, o2) -> o2.getLikes().size() - o1.getLikes().size())
                .collect(Collectors.toList());
    }
}
