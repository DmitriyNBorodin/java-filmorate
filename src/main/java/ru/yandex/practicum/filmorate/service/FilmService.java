package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmService {
    List<Film> getAllFilms();

    Film addFilm(Film film);

    Optional<Film> deleteFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(int filmId, int userId);

    boolean removeLike(int filmId, int userId);

    List<Film> getMostLikedFilms(int amount);


}
