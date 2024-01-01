package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
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
}
