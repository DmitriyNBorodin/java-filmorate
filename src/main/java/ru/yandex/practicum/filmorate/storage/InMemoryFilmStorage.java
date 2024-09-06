package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Integer, Film> filmMap = new HashMap<>();

    public List<Film> getAllFilms() {
        return new ArrayList<>(filmMap.values());
    }

    public Film getFilmById(int filmId) {
        return filmMap.get(filmId);
    }

    public Film addFilm(Film film) {
        film.setId(getNextId());
        filmMap.put(film.getId(), film);
        return film;
    }

    public Optional<Film> deleteFilm(Film film) {
        return Optional.ofNullable(filmMap.remove(film.getId()));
    }

    public Film updateFilm(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }

    public Optional<Film> findFilm(int filmId) {
        return Optional.ofNullable(filmMap.get(filmId));
    }

    public boolean checkFilm(int filmId) {
        if (filmMap.containsKey(filmId)) return true;
        else throw new NotFoundException("Фильм не найден");
    }

    private int getNextId() {
        int currentMaxId = filmMap.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }
}
