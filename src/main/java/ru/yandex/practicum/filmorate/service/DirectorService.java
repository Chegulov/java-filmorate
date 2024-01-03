package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;


@Service
public class DirectorService {

    private final DirectorStorage directorDbStorage;

    @Autowired
    public DirectorService(DirectorStorage directorDbStorage) {
        this.directorDbStorage = directorDbStorage;
    }

    public List<Director> getDirectors() {
        return directorDbStorage.getDirectors();
    }

    public Director create(Director director) {
        return directorDbStorage.create(director);
    }

    public Director update(Director director) {
        return directorDbStorage.update(director);
    }

    public Director getDirectorById(int id) {
        return directorDbStorage.getDirectorById(id);
    }

    public void deleteDirectorById(int id) {
        directorDbStorage.deleteDirectorById(id);
    }


}
