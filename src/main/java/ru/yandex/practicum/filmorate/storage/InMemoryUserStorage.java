package ru.yandex.practicum.filmorate.storage;

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

}
