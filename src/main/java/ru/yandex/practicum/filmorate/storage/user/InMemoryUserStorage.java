package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCount = 0;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        idCount++;
        user.setId(idCount);
        users.put(idCount, user);

        return user;
    }

    @Override
    public User update(User user) {
        if (users.containsKey(user.getId())) {
            log.info("Пользователь с id={} обновлён", user.getId());
            users.put(user.getId(), user);
        } else {
            String msg = String.format("Пользователь с id=%d не найден", user.getId());
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        return user;
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);

        if (user == null) {
            String msg = String.format("Пользователь с id=%d не найден", id);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        return user;
    }

    @Override
    public User addFriend(int userId, int friendId) {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        user1.getFriends().add(friendId);
        user2.getFriends().add(userId);
        log.info("Пользователь с id={} добавлен в список друзей пользователя с id={}", friendId, userId);
        return user1;
    }

    @Override
    public User deleteFriend(int userId, int friendId) {
        User user1 = getUserById(userId);
        User user2 = getUserById(friendId);

        if (!user1.getFriends().contains(friendId)) {
            String msg = String.format("Пользователя с id=%d нет в списке друзей у пользователя с id=%d", friendId, userId);
            log.info(msg);
            throw new DataNotFoundException(msg);
        }

        user1.getFriends().remove(friendId);
        user2.getFriends().remove(userId);
        log.info("Пользователь с id={} удалён из списка друзей пользователя с id={}", friendId, userId);
        return user1;
    }

    @Override
    public List<Integer> getFriends(int id) {
        return new ArrayList<>(getUserById(id).getFriends());
    }
}
