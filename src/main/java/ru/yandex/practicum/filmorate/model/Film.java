package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.MinimumDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class Film {
    private int id;
    @NotBlank
    private String name;
    @Size(max = 200)
    private String description;
    @NotNull
    @MinimumDate
    private LocalDate releaseDate;
    @Min(1)
    private Integer duration;
    private final Set<Integer> likes = new HashSet<>();
    private final TreeSet<Genre> genres = new TreeSet<>();
    @NotNull
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
