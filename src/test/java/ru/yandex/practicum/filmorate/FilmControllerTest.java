package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class FilmControllerTest {
    private final FilmController controller = new FilmController();
    Film testFilm;

    @BeforeEach
    public void prepareFilmFoTests() {
        testFilm = new Film(1, "testName", "testDescription",
                LocalDate.of(2000, 1, 1), 180);
    }

    @Test
    public void filmWithoutNameTest() {
        testFilm.setName("   ");
        assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
    }

    @Test
    public void filmWithLongDescriptionTest() {
        int leftLimit = 97;
        int rightLimit = 122;
        int targetStringLength = 300;
        Random random = new Random();

        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        testFilm.setDescription(generatedString);
        assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
    }

    @Test
    public void filmWithWrongReleaseDateTest() {
        testFilm.setReleaseDate(LocalDate.of(1800, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
    }

    @Test
    public void negativeFilmDurationTest() {
        testFilm.setDuration(-1);
        assertThrows(ValidationException.class, () -> controller.addFilm(testFilm));
    }
}
