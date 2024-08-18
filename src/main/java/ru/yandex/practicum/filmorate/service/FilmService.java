package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class FilmService {
    private FilmStorage storage;
    private UserService userService;

    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    public Film addFilm(Film film) {
        validateFilm(film);
        storage.addFilm(film);
        log.info("Добавлен фильм {} c id {}", film.getName(), film.getId());
        return film;
    }

    public Optional<Film> deleteFilm(Film film) {
        if (storage.checkFilm(film.getId())) {
            log.info("Удален фильм {} с id {}", film.getName(), film.getId());
        }
        return storage.deleteFilm(film);
    }

    public Film updateFilm(Film film) {
        validateFilm(film);
        if (!storage.checkFilm(film.getId())) {
            log.info("Не удалось найти фильм с id {} для обновления", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с таким id отсутствует");

        } else {
            log.info("Обновлены данные о фильме {} с id {}", film.getName(), film.getId());
            return storage.updateFilm(film);
        }
    }

    public Film addLike(int filmId, int userId) {
        log.info("Добавление лайка пользователем с id {} для фильма с id {}", userId, filmId);
        storage.checkFilm(filmId);
        userService.checkUserById(userId);
        storage.getFilmById(filmId).addLike(userId);
        return storage.getFilmById(filmId);
    }

    public boolean removeLike(int filmId, int userId) {
        storage.checkFilm(filmId);
        userService.checkUserById(userId);
        storage.getFilmById(filmId).removeLike(userId);
        return true;
    }

    public List<Film> getMostLikedFilms(int amount) {
        log.info("Получение списка популярных фильмов");
        return storage.getAllFilms().stream()
                .sorted(Comparator.comparing(Film::getAmountOfLikes).reversed())
                .limit(amount)
                .collect(Collectors.toList());
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
