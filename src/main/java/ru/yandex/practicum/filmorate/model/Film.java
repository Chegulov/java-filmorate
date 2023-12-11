package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;

@Data
@Builder
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private final Set<Integer> likes = new HashSet<>();
    private final TreeSet<Genre> genres = new TreeSet<>();
    private Mpa mpa;

    public Map<String, Object> toMap() {
        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("name", name);
        filmMap.put("description", description);
        filmMap.put("release_date", releaseDate);
        filmMap.put("duration", duration);
        filmMap.put("mpa_id", mpa.getId());
        return filmMap;
    }
}
