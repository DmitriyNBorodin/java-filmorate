package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Optional;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmorateApplicationTests {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    @Test
    public void testFindUserById() {
        User testUser = new User("123@yandex.ru", "testLogin",
                LocalDate.of(2000, 1, 1));
                userStorage.addUser(testUser);
        userStorage.addUser(testUser);
        Optional<User> userOptional = userStorage.getUserById(1);
        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    public void testFindFilmById() {
        Film testFilm = new Film(1, "testName", "testDescription",
                LocalDate.of(2000, 1, 1), 180);
        filmStorage.addFilm(testFilm);
        Film extractingFilm = filmStorage.getFilmById(1);
        Assertions.assertThat(extractingFilm).hasFieldOrPropertyWithValue("id", 1);
    }
}