package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getUsers();

    User create(User user);

    User update(User user);

    User getUserById(int id);

    User addFriend(int userId, int friendId);

    User deleteFriend(int userId, int friendId);

    List<Integer> getFriends(int id);
}
