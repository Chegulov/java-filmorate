package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCount = 0;

    @GetMapping("/users")
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        validate(user);

        idCount++;
        user.setId(idCount);
        users.put(idCount, user);

        log.info("Пользователь с id={} добавлен", user.getId());
        return user;
    }

    @PutMapping("/users")
    public ResponseEntity<User> update(@RequestBody User user) {
        validate(user);
        if (users.containsKey(user.getId())) {
            log.info("Пользователь с id={} обновлён", user.getId());
            users.put(user.getId(), user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } else {
            log.info("Пользователь с id={} не найден", user.getId());
            return new ResponseEntity<>(user, HttpStatus.NOT_FOUND);
        }
    }

    private void validate(User user) {
        String msg;
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            msg = "Почта не может быть пустой";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (!user.getEmail().contains("@")) {
            msg = "Почта должна содержать \"@\"";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            msg = "Логин не может быть пустым и содержать пробелы";
            log.error(msg);
            throw new ValidationException(msg);
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("В качестве имени выбран логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            msg = "Дата рождения не может быть в будущем.";
            log.error(msg);
            throw new ValidationException(msg);
        }
    }
}
