package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class UserControllerTest {
    private final UserController controller = new UserController();
    User testUser;

    @BeforeEach
    public void prepareTestUser() {
        testUser = new User("123@yandex.ru", "testLogin",
                LocalDate.of(2000, 1, 1));
    }

    @Test
    public void userWithoutEmailTest() {
        testUser.setEmail(null);
        assertThrows(ValidationException.class, () -> controller.addUser(testUser));
    }

    @Test
    public void userWithoutLoginTest() {
        testUser.setLogin(null);
        assertThrows(ValidationException.class, () -> controller.addUser(testUser));
        testUser.setLogin("A     Z");
        assertThrows(ValidationException.class, () -> controller.addUser(testUser));
    }

    @Test
    public void userWithWrongBirthdate() {
        testUser.setBirthday(LocalDate.of(3000, 1, 1));
        assertThrows(ValidationException.class, () -> controller.addUser(testUser));
    }
}
