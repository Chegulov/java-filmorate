package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Collection<Director> getDirectors();

    Director create(Director director);

    Director update(Director director);

    Director getDirectorById(int id);

    void deleteDirectorById(int id);
}
