package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getDirectors() {
        return directorService.getDirectorDbStorage().getDirectors();
    }


    @PostMapping
    public ResponseEntity<Director> create(@RequestBody Director director) {
        try {
            log.info(" Режиссёр с id={} добавлен", director.getId());
            return new ResponseEntity<>(directorService.getDirectorDbStorage().create(director), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping
    public Director update(@RequestBody Director director) {
        directorService.getDirectorDbStorage().update(director);

        return director;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Director> getDirectorById(@PathVariable int id) {
        try {
            return new ResponseEntity<>(directorService.getDirectorDbStorage().getDirectorById(id), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("/{id}")
    public void deleteDirectorById(@PathVariable int id) {
        directorService.getDirectorDbStorage().deleteDirectorById(id);
    }

}
