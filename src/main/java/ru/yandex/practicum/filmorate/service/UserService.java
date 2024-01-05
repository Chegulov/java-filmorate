package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final FeedService feedService;

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage,
                       FeedService feedService) {
        this.userStorage = userStorage;
        this.feedService = feedService;
    }

    public UserStorage getUserStorage() {
        return userStorage;
    }

    public User addFriend(int id, int friendId) {
        User user = userStorage.addFriend(id, friendId);
        feedService.create(Feed.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.ADD)
                .timestamp(Instant.now().toEpochMilli())
                .userId(id)
                .entityId(friendId)
                .build());
        return user;
    }

    public User deleteFriend(int id, int friendId) {
        User user = userStorage.deleteFriend(id, friendId);
        feedService.create(Feed.builder()
                .eventType(EventType.FRIEND)
                .operation(Operation.REMOVE)
                .timestamp(Instant.now().toEpochMilli())
                .userId(id)
                .entityId(friendId)
                .build());
        return user;
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
