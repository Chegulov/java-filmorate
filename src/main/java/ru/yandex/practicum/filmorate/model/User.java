package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private final Set<Integer> friends = new HashSet<>();

    public Map<String, Object> toMap() {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("login", login);
        userMap.put("name", name);
        userMap.put("birthday", birthday);
        return userMap;
    }
}
