package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Director implements Comparable<Director>{
    private Long id;
    private String  name;

    @Override
    public int compareTo(Director o) {
        return this.id.compareTo(o.id);
    }
}
