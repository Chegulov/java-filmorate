package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.storage.event.FeedStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
public class FeedService {
    private final FeedStorage feedStorage;
    private final UserStorage userStorage;

    @Autowired
    public FeedService(FeedStorage feedStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.feedStorage = feedStorage;
        this.userStorage = userStorage;
    }

    public List<Feed> getFeed(int userId) {
        userStorage.getUserById(userId);
        return feedStorage.getFeed(userId);
    }

    public void create(Feed feed) {
        feedStorage.create(feed);
    }
}
