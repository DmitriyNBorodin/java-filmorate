package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.CodeEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;

import java.util.List;

public interface FilmService {
    List<Film> getAllFilms();

    FilmDto getFilmById(int filmId);

    Film addFilm(Film film);

    boolean deleteFilm(Film film);

    Film updateFilm(Film film);

    Film addLike(int filmId, int userId);

    boolean removeLike(int filmId, int userId);

    List<Film> getMostLikedFilms(int amount);

    CodeEntity getMpaByCode(int mpaCode);

    List<CodeEntity> getAllMpaCodes();

    CodeEntity getGenreByCode(int genreCode);

    List<CodeEntity> getAllGenres();
}
