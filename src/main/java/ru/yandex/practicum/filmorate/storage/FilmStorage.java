package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Map;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getFilmById(int filmId);

    Film addFilm(Film film);

    boolean deleteFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int filmId, int userId);

    public void removeLike(int filmId, int userId);

    public List<Film> getMostLikedFilms(int amount);

    boolean checkFilm(int filmId);

    Map<Integer, String> getMpaMap();

    Map<Integer, String> getGenresMap();
}
