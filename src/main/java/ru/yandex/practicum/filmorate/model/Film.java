package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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
    private final TreeSet<Director> directors = new TreeSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> filmMap = new HashMap<>();
        filmMap.put("name", name);
        filmMap.put("description", description);
        filmMap.put("release_date", releaseDate);
        filmMap.put("duration", duration);
        filmMap.put("mpa_id", mpa.getId());
        return filmMap;
    }

    public List<String> getDirectorsName() {
        return directors.stream().map(Director::getName).collect(Collectors.toList());
    }
}
