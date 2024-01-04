package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Feed;

import java.util.List;

public interface FeedStorage {
    List<Feed> getFeed(int userId);

    void create(Feed feed);
}
