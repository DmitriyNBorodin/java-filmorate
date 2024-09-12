package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.mappers.FilmRowMapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FilmRowMapper filmRowMapper;
    private static final String GET_ALL_QUERY = "SELECT * FROM films";
    private static final String GET_ALL_FILM_GENRES_QUERY = "SELECT * FROM film_genre";
    private static final String GET_ALL_FILM_LIKES_QUERY = "SELECT * FROM film_likes";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String GET_GENRES_BY_ID_QUERY = "SELECT genre_id FROM film_genre WHERE film_id = ?";
    private static final String GET_LIKES_BY_ID_QUERY = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String ADD_FILM_QUERY = "INSERT INTO films (name, description," +
            " release_date, duration, mpa_rating) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?," +
            " release_date = ?, duration = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String CHECK_QUERY = "SELECT COUNT (name) FROM films WHERE id = ?";
    private static final String ADD_LIKE_QUERY = "INSERT INTO film_likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String LIKED_FILMS_QUERY = "SELECT * FROM (SELECT film_id, COUNT(user_id) FROM film_likes" +
            " GROUP BY film_id ORDER BY count(user_id) DESC LIMIT ?) AS best_films LEFT JOIN films ON films.id = best_films.film_id";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    private static final String GET_MPA_MAP_QUERY = "SELECT * FROM mpa_codes";
    private static final String GET_GENRE_MAP_QUERY = "SELECT * FROM genre_codes";

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilmsList = jdbcTemplate.query(GET_ALL_QUERY, filmRowMapper);
        Map<Integer, Film> allFilmsMap = new HashMap<>();
        for (Film film : allFilmsList) {
            allFilmsMap.put(film.getId(), film);
        }
        jdbcTemplate.query(GET_ALL_FILM_GENRES_QUERY, rs -> {
            while (rs.next()) {
                allFilmsMap.get(rs.getInt("film_id")).addGenre(rs.getInt("genre_id"));
            }
            return null;
        });
        jdbcTemplate.query(GET_ALL_FILM_LIKES_QUERY, rs -> {
            while (rs.next()) {
                allFilmsMap.get(rs.getInt("film_id")).addLike(rs.getInt("user_id"));
            }
            return null;
        });
        return new ArrayList<>(allFilmsMap.values());
    }

    @Override
    public Film getFilmById(int filmId) {
        Film extractingFilm = jdbcTemplate.queryForObject(GET_BY_ID_QUERY, filmRowMapper, filmId);
        List<Integer> extractingGenres = jdbcTemplate.queryForList(GET_GENRES_BY_ID_QUERY, Integer.class, filmId);
        if (!extractingGenres.isEmpty()) {
            for (int genre : extractingGenres) {
                extractingFilm.addGenre(genre);
            }
        }
        List<Integer> extractingLikes = jdbcTemplate.queryForList(GET_LIKES_BY_ID_QUERY, Integer.class, filmId);
        if (!extractingLikes.isEmpty()) {
            for (int like : extractingLikes) {
                extractingFilm.addLike(like);
            }
        }
        return extractingFilm;
    }

    @Override
    public Film addFilm(Film film) {
        int newFilmId = insert(ADD_FILM_QUERY, film.getName(), film.getDescription(), Date.valueOf(film.getReleaseDate()),
                film.getDuration(), film.getMpa().get("id"));
        film.setId(newFilmId);
        if (Optional.ofNullable(film.getGenres()).isPresent())
            for (Map genre : film.getGenres()) {
                try {
                    jdbcTemplate.update(INSERT_GENRE_QUERY, newFilmId, genre.get("id"));
                } catch (RuntimeException e) {
                    System.out.println(e.getMessage());
                }
            }
        return film;
    }

    @Override
    public boolean deleteFilm(Film film) {
        return jdbcTemplate.update(DELETE_QUERY, film.getId()) > 0;
    }

    @Override
    public Film updateFilm(Film film) {
        jdbcTemplate.update(UPDATE_QUERY, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getId());
        return film;
    }

    @Override
    public boolean checkFilm(int filmId) {
        Optional<Integer> filmCheck = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_QUERY, Integer.class, filmId));
        return filmCheck.isPresent() ? filmCheck.get() != 0 : false;
    }

    @Override
    public void addLike(int filmId, int userId) {
        jdbcTemplate.update(ADD_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        jdbcTemplate.update(REMOVE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public List<Film> getMostLikedFilms(int amount) {
        return jdbcTemplate.query(LIKED_FILMS_QUERY, filmRowMapper, amount);
    }

    protected Integer insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        int id = keyHolder.getKeyAs(Integer.class);
        if (id != 0) {
            return id;
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    @Override
    public Map<Integer, String> getMpaMap() {
        return jdbcTemplate.query(GET_MPA_MAP_QUERY, (ResultSetExtractor<Map<Integer, String>>) rs -> {
            HashMap<Integer, String> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getInt("id"), rs.getString("code_name"));
            }
            return result;
        });
    }

    @Override
    public Map<Integer, String> getGenresMap() {
        return jdbcTemplate.query(GET_GENRE_MAP_QUERY, (ResultSetExtractor<Map<Integer, String>>) rs -> {
            HashMap<Integer, String> result = new HashMap<>();
            while (rs.next()) {
                result.put(rs.getInt("id"), rs.getString("genre_name"));
            }
            return result;
        });
    }
}
