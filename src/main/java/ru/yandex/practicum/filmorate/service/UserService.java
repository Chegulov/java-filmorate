package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public User addFriend(int id, int friendId) {
        return userStorage.addFriend(id, friendId);
    }

    public User deleteFriend(int id, int friendId) {
        return userStorage.deleteFriend(id, friendId);
    }

    public List<User> getFriendsList(int id) {
        return userStorage.getFriends(id).stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(int id, int otherId) {
        Set<Integer> friends = new HashSet<>(userStorage.getFriends(id));
        Set<Integer> otherFriends = new HashSet<>(userStorage.getFriends(otherId));

        friends.retainAll(otherFriends);
        return friends.stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
