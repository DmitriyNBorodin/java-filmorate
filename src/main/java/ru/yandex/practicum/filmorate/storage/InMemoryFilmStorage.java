package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public boolean deleteFilm(Film film) {
        return filmMap.remove(film.getId()) != null;
    }

    public Film updateFilm(Film film) {
        filmMap.put(film.getId(), film);
        return film;
    }


    public boolean checkFilm(int filmId) {
        if (filmMap.containsKey(filmId)) return true;
        else throw new NotFoundException("Фильм не найден");
    }

    public void addLike(int filmId, int userId) {
        filmMap.get(filmId).addLike(userId);
    }

    public void removeLike(int filmId, int userId) {
        filmMap.get(filmId).removeLike(userId);
    }

    public List<Film> getMostLikedFilms(int amount) {
        return filmMap.values().stream()
                .sorted(Comparator.comparing(Film::obtainAmountOfLikes).reversed())
                .limit(amount)
                .collect(Collectors.toList());
    }

    private int getNextId() {
        int currentMaxId = filmMap.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    @Override
    public Map<Integer, String> getMpaMap() {
        return null;
    }

    @Override
    public Map<Integer, String> getGenresMap() {
        return null;
    }
}
