package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController controller;
    User testUser;

    @BeforeEach
    public void prepareTestUser() {
        testUser = new User("123@yandex.ru", "testLogin",
                LocalDate.of(2000, 1, 1));
    }

    @Test
    public void userWithoutEmailTest() {
        testUser.setEmail(null);
        try {
            controller.addUser(testUser);
        } catch (ValidationException e) {
            assertEquals("Некорректный E-mail", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение в некорректном E-mail");
    }

    @Test
    public void userWithoutLoginTest() {
        testUser.setLogin(null);
        try {
            controller.addUser(testUser);
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым или содержать пробелы", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение о некорректном логине");
    }

    @SuppressWarnings("checkstyle:WhitespaceAround")
    @Test
    public void userWithImpossibleLogin() {
        testUser.setLogin("A     Z");
        try {
            controller.addUser(testUser);
        } catch (ValidationException e) {
            assertEquals("Логин не может быть пустым или содержать пробелы", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение о некорректном логине");
    }

    @Test
    public void userWithWrongBirthdate() {
        testUser.setBirthday(LocalDate.of(3000, 1, 1));
        try {
            controller.addUser(testUser);
        } catch (ValidationException e) {
            assertEquals("Некорректная дата рождения", e.getMessage());
            return;
        }
        fail("Не удалось получить сообщение о некорректной дате рождения");
    }
}
