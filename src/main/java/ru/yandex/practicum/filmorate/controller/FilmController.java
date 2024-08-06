package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Integer, Film> filmMap = new HashMap<>();


    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(filmMap.values());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        if (validateFilm(film)) {
            film.setId(getNextId());
            filmMap.put(film.getId(), film);
            log.info("Добавлен фильм {} c id {}", film.getName(), film.getId());
        }
        return film;
    }

    @PutMapping()
    public Film updateFilm(@RequestBody Film film) {
        if (!validateFilm(film) || !filmMap.containsKey(film.getId())) {
            log.info("Не удалось найти фильм с id {} для обновления", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с таким id отсутствует");

        } else {
            filmMap.put(film.getId(), film);
            log.info("Обновлены данные о фильме {} с id {}", film.getName(), film.getId());
        }
        return film;
    }

    private int getNextId() {
        int currentMaxId = filmMap.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private boolean validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.info("Попытка создать фильм с пустым названием");
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            log.info("Попытка создать описание фильма длиной более 200 символов");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.info("Дата создания фильма не должна быть ранее 28 декабря 1895 года");
            throw new ValidationException("Дата создания фильма не должна быть ранее 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            log.info("Попытка создать фильм с отрицательной или нулевой продолжительностью");
            throw new ValidationException("Продолжительность фильмы должна быть положительным числом");
        }
        return true;
    }
}
