package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Feed {
    private long timestamp;
    private int userId;
    private EventType eventType;
    private Operation operation;
    private int eventId;
    private int entityId;
}
