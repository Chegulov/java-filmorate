package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final FeedService feedService;
    private final FilmService filmService;


    @GetMapping
    public List<User> getUsers() {
        return userService.getUserStorage().getUsers();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        userService.getUserStorage().create(user);

        log.info("Пользователь с id={} добавлен", user.getId());
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
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

    @GetMapping ("/{id}/feed")
    public List<Feed> getFeed(@PathVariable int id) {
        return feedService.getFeed(id);
    }

    @GetMapping("/{id}/recommendations")
    public List<Film> getRecommendationsForUser(@PathVariable int id) {
        return filmService.getRecommendation(id);

    }
}
