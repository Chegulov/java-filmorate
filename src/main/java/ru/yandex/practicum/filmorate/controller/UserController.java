package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUserStorage().getUsers();
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        userService.getUserStorage().create(user);

        log.info("Пользователь с id={} добавлен", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        validate(user);
        userService.getUserStorage().update(user);

        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFriend(id, friendId);
    }

    @DeleteMapping("/{id}")
    public void deleteFriend(@PathVariable int id) {
        userService.getUserStorage().deleteUserById(id);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriendsList(@PathVariable int id) {
        return userService.getFriendsList(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserStorage().getUserById(id);
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
