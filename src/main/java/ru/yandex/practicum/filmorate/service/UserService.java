package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    boolean checkUserById(int userId);

    List<User> getUsers();

    User addUser(User user);

    User updateUser(User user);

    boolean addFriend(int userId, int newFriendId);

    List<User> removeFriend(int userId, int removingFriendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int anotherUserId);
}
