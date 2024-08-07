package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Integer, User> userMap = new HashMap<>();

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        if (validateUserInfo(user)) {
            user.setId(getNextId());
            userMap.put(user.getId(), user);
            log.info("Добавлен новый пользователь {} с id {}", user.getName(), user.getId());
        }
        return user;
    }

    @PutMapping()
    public User updateUser(@RequestBody User user) {
        if (!validateUserInfo(user) || !userMap.containsKey(user.getId())) {
            log.info("Не удалось найти пользователя с id {} для обновления", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с таким id отсутствует");

        } else {
            userMap.put(user.getId(), user);
            log.info("Обновлены данные о пользователе {} с id {}", user.getName(), user.getId());
        }
        return user;
    }

    private int getNextId() {
        int currentMaxId = userMap.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    private boolean validateUserInfo(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.info("Попытка создать пользователя с некорректным e-mail");
            throw new ValidationException("Некорректный E-mail");
        } else if (user.getLogin() == null || user.getLogin().isBlank() ||
                user.getLogin().contains(" ")) {
            log.info("Попытка создать пользователя с некорректным логином");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.info("Попытка создать пользователя с некорректной датой рождения");
            throw new ValidationException("Некорректная дата рождения");
        }
        return true;
    }
}
