package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public User addFriend(int id, int friendId) {
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);

        user1.getFriends().add(friendId);
        user2.getFriends().add(id);
        log.info("Пользователь с id={} добавлен в список друзей пользователя с id={}", friendId, id);
        return user1;
    }

    public User deleteFriend(int id, int friendId) {
        User user1 = userStorage.getUserById(id);
        User user2 = userStorage.getUserById(friendId);

        if (!user1.getFriends().contains(friendId)) {
            String msg = String.format("Пользователя с id=%d нет в списке друзей у пользователя с id=%d", friendId, id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        user1.getFriends().remove(friendId);
        user2.getFriends().remove(id);
        log.info("Пользователь с id={} удалён из списка друзей пользователя с id={}", friendId, id);
        return user1;
    }

    public List<User> getFriendsList(int id) {
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> friends = new HashSet<>(userStorage.getUserById(id).getFriends());
        Set<Integer> otherFriends = new HashSet<>(userStorage.getUserById(otherId).getFriends());

        friends.retainAll(otherFriends);
        return friends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
