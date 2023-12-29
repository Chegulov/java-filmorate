package ru.yandex.practicum.filmorate.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;


@Service
@Data
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDbStorage directorDbStorage;

}
