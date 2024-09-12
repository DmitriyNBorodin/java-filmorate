package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    List<User> getUsers();

    Optional<User> getUserById(int id);

    User addUser(User user);

    boolean deleteUser(User user);

    User updateUser(User user);

    boolean checkUser(int userId);

    boolean addFriend(int userId, int friendId);

    Set<Integer> getUserFriendList(int userId);

    void removeFriend(int userId, int friendId);
}
