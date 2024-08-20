package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class UserServiceSimple implements UserService {
    private UserStorage storage;

    @Override
    public boolean checkUserById(int userId) {
        return storage.checkUser(userId);
    }

    @Override
    public List<User> getUsers() {
        return storage.getUsers();
    }

    @Override
    public User addUser(User user) {
        validateUserInfo(user);
        storage.addUser(user);
        log.info("Добавлен новый пользователь {} с id {}", user.getName(), user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        validateUserInfo(user);
        if (!storage.checkUser(user.getId())) {
            log.info("Не удалось найти пользователя с id {} для обновления", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с таким id отсутствует");
        }
        log.info("Обновлены данные о пользователе {} с id {}", user.getName(), user.getId());
        return storage.updateUser(user);
    }

    @Override
    public boolean addFriend(int userId, int newFriendId) {
        storage.checkUser(userId);
        storage.checkUser(newFriendId);
        User currentUser = storage.getUserById(userId);
        if (currentUser.getFriendList().contains(newFriendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        currentUser.addFriend(newFriendId);
        storage.getUserById(newFriendId).addFriend(userId);
        return true;
    }

    @Override
    public List<User> removeFriend(int userId, int removingFriendId) {
        log.info("Запрос на удаление друга с id {} у пользователя с id {}", removingFriendId, userId);
        storage.checkUser(userId);
        storage.checkUser(removingFriendId);
        User currentUser = storage.getUserById(userId);
        if (currentUser.getFriendList().contains(removingFriendId)) {
            currentUser.removeFriend(removingFriendId);
            storage.getUserById(removingFriendId).removeFriend(userId);
        }
        return getFriends(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        storage.checkUser(userId);
        return storage.getUserById(userId).getFriendList().stream()
                .map(friendId -> storage.getUserById(friendId))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int userId, int anotherUserId) {
        return storage.getUserById(userId).getFriendList().stream()
                .filter(storage.getUserById(anotherUserId).getFriendList()::contains)
                .map(friendId -> storage.getUserById(friendId))
                .collect(Collectors.toList());
    }

    private void validateUserInfo(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            UserServiceSimple.log.info("Попытка создать пользователя с некорректным e-mail");
            throw new ValidationException("Некорректный E-mail");
        } else if (user.getLogin() == null || user.getLogin().isBlank() ||
                user.getLogin().contains(" ")) {
            UserServiceSimple.log.info("Попытка создать пользователя с некорректным логином");
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            UserServiceSimple.log.info("Попытка создать пользователя с некорректной датой рождения");
            throw new ValidationException("Некорректная дата рождения");
        }
    }
}
