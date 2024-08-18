package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    List<User> getUsers();

    User getUserById(int id);

    User addUser(User user);

    Optional<User> deleteUser(User user);

    User updateUser(User user);

    boolean checkUser(int userId);
}
