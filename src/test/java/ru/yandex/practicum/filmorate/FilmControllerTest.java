package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class FilmControllerTest {

    @Autowired
    private FilmController controller;

    Film testFilm;

    @BeforeEach
    public void prepareFilmFoTests() {
        testFilm = new Film(1, "testName", "testDescription",
                LocalDate.of(2000, 1, 1), 180);
    }

    @Test
    public void filmWithoutNameTest() {
        testFilm.setName("   ");
        try {
            controller.addFilm(testFilm);
        } catch (ValidationException e) {
            assertEquals("Название фильма не может быть пустым", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение о добавлении фильма без названия");
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
        try {
            controller.addFilm(testFilm);
        } catch (ValidationException e) {
            assertEquals("Максимальная длина описания - 200 символов", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение о слишком длинном описании фильма");
    }

    @Test
    public void filmWithWrongReleaseDateTest() {
        testFilm.setReleaseDate(LocalDate.of(1800, 1, 1));
        try {
            controller.addFilm(testFilm);
        } catch (ValidationException e) {
            assertEquals("Дата создания фильма не должна быть ранее 28 декабря 1895 года", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение о некорректной дате создания фильма");
    }

    @Test
    public void negativeFilmDurationTest() {
        testFilm.setDuration(-1);
        try {
            controller.addFilm(testFilm);
        } catch (ValidationException e) {
            assertEquals("Продолжительность фильмы должна быть положительным числом", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение об отрицательной или нулевой продолжительности фильма");
    }
}
