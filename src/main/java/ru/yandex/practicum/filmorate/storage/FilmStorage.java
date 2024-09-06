package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    List<Film> getAllFilms();

    Film getFilmById(int filmId);

    Film addFilm(Film film);

    Optional<Film> deleteFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> findFilm(int filmId);

    boolean checkFilm(int filmId);
}
