package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class Review {
    private int reviewId;
    @NotBlank
    private String content;
    @NotNull
    private Boolean isPositive;
    private Integer userId;
    private Integer filmId;
    private final Map<Integer, Boolean> reviewsLikes = new HashMap<>();

    public Map<String, Object> toMap() {
        Map<String, Object> reviewMap = new HashMap<>();
        reviewMap.put("content", content);
        reviewMap.put("is_positive", isPositive);
        reviewMap.put("user_Id", userId);
        reviewMap.put("film_id", filmId);
        return reviewMap;
    }

    public int getUseful() {
        int useful = 0;
        for (Boolean b : reviewsLikes.values()) {
            if (b) {
                useful++;
            } else {
                useful--;
            }
        }
        return useful;
    }
}
