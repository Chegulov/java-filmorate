package ru.yandex.practicum.filmorate.storage.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Feed;
import ru.yandex.practicum.filmorate.model.Operation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class FeedDbStorage implements FeedStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FeedDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Feed> getFeed(int userId) {
        String sqlQuery = "SELECT * FROM feed WHERE user_id=?";
        return jdbcTemplate.query(sqlQuery, this::makeFeed, userId);
    }

    @Override
    public void create(Feed feed) {
        String sqlQuery = "INSERT into feed (event_type, operation, timestamp, user_id, entity_id)"
                + "values (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sqlQuery,
                feed.getEventType().name(),
                feed.getOperation().name(),
                feed.getTimestamp(),
                feed.getUserId(),
                feed.getEntityId()
                );
    }

    private Feed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return Feed.builder()
                .eventId(rs.getInt("event_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .timestamp(rs.getLong("timestamp"))
                .userId(rs.getInt("user_id"))
                .entityId(rs.getInt("entity_id"))
                .build();
    }
}
