package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.CodeEntity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.FilmDto;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class FilmServiceSimple implements FilmService {
    private FilmStorage storage;
    private UserService userService;

    @Override
    public List<Film> getAllFilms() {
        return storage.getAllFilms();
    }

    @Override
    public FilmDto getFilmById(int filmId) {
        return convertToDto(storage.getFilmById(filmId));
    }

    private FilmDto convertToDto(Film film) {
        FilmDto filmDto = new FilmDto(film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getLikes());
        Map<Integer, String> genreNames = storage.getGenresMap();
        Map<Integer, String> mpaCodes = storage.getMpaMap();
        int mpaCode = film.getMpa().get("id");
        filmDto.setMpa(new CodeEntity(mpaCode, mpaCodes.get(mpaCode)));
        for (Map<String, Integer> genre : film.getGenres()) {
            int genreId = genre.get("id");
            filmDto.addGenre(new CodeEntity(genreId, genreNames.get(genreId)));
        }
        return filmDto;
    }

    @Override
    public Film addFilm(Film film) {
        validateFilm(film);
        storage.addFilm(film);
        log.info("Добавлен фильм {} c id {}", film.getName(), film.getId());
        return film;
    }

    @Override
    public boolean deleteFilm(Film film) {
        if (storage.checkFilm(film.getId())) {
            log.info("Удален фильм {} с id {}", film.getName(), film.getId());
        }
        return storage.deleteFilm(film);
    }

    @Override
    public Film updateFilm(Film film) {
        validateFilm(film);
        if (!storage.checkFilm(film.getId())) {
            log.info("Не удалось найти фильм с id {} для обновления", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с таким id отсутствует");
        }
        log.info("Обновлены данные о фильме {} с id {}", film.getName(), film.getId());
        return storage.updateFilm(film);
    }

    @Override
    public Film addLike(int filmId, int userId) {
        log.info("Добавление лайка пользователем с id {} для фильма с id {}", userId, filmId);
        storage.checkFilm(filmId);
        userService.checkUserById(userId);
        storage.addLike(filmId, userId);
        return storage.getFilmById(filmId);
    }

    @Override
    public boolean removeLike(int filmId, int userId) {
        storage.checkFilm(filmId);
        userService.checkUserById(userId);
        storage.removeLike(filmId, userId);
        return true;
    }

    @Override
    public List<Film> getMostLikedFilms(int amount) {
        log.info("Получение списка популярных фильмов");
        return storage.getMostLikedFilms(amount);
    }

    private boolean validateGenre(Film film) {
        Set<Integer> genreCodes = storage.getGenresMap().keySet();
        for (Map<String, Integer> filmGenre : film.getGenres()) {
            if (!genreCodes.contains(filmGenre.get("id"))) {
                return false;
            }
        }
        return true;
    }

    public CodeEntity getMpaByCode(int mpaCode) {
        Map<Integer, String> mpaMap = storage.getMpaMap();
        if (!mpaMap.containsKey(mpaCode)) {
            throw new NotFoundException("Неизвестный код mpa " + mpaCode);
        }
        return new CodeEntity(mpaCode, mpaMap.get(mpaCode));
    }

    @Override
    public List<CodeEntity> getAllMpaCodes() {
        return storage.getMpaMap().entrySet().stream().map(entry -> new CodeEntity(entry.getKey(), entry.getValue())).toList();
    }

    @Override
    public CodeEntity getGenreByCode(int genreCode) {
        Map<Integer, String> genresMap = storage.getGenresMap();
        if (!genresMap.containsKey(genreCode)) {
            throw new NotFoundException("Неизвестный код жанра " + genreCode);
        }
        return new CodeEntity(genreCode, genresMap.get(genreCode));
    }

    @Override
    public List<CodeEntity> getAllGenres() {
        return storage.getGenresMap().entrySet().stream().map(entry -> new CodeEntity(entry.getKey(), entry.getValue())).toList();
    }

    public void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            FilmServiceSimple.log.info("Попытка создать фильм с пустым названием");
            throw new ValidationException("Название фильма не может быть пустым");
        } else if (film.getDescription().length() > 200) {
            FilmServiceSimple.log.info("Попытка создать описание фильма длиной более 200 символов");
            throw new ValidationException("Максимальная длина описания - 200 символов");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            FilmServiceSimple.log.info("Дата создания фильма не должна быть ранее 28 декабря 1895 года");
            throw new ValidationException("Дата создания фильма не должна быть ранее 28 декабря 1895 года");
        } else if (film.getDuration() <= 0) {
            FilmServiceSimple.log.info("Попытка создать фильм с отрицательной или нулевой продолжительностью");
            throw new ValidationException("Продолжительность фильмы должна быть положительным числом");
        } else if (!storage.getMpaMap().containsKey(film.getMpa().get("id"))) {
            FilmServiceSimple.log.info("Попытка создать фильм с несуществующим mpa");
            throw new ValidationException("Код mpa должен присутствовать в таблице");
        } else if (!validateGenre(film)) {
            FilmServiceSimple.log.info("Попытка создать фильм с несуществующим кодом жанра");
            throw new ValidationException("Код жанра должен присутствовать в таблице");
        }
    }
}
