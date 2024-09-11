package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class InMemoryUserStorage implements UserStorage {
    private Map<Integer, User> userMap = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(userMap.get(id));
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean deleteUser(User user) {
        return userMap.remove(user.getId()) != null;
    }

    @Override
    public boolean checkUser(int userId) {
        if (userMap.containsKey(userId)) return true;
        else throw new NotFoundException("Пользователь не найден");
    }

    @Override
    public void removeFriend(int userId, int removingFriendId) {
        userMap.get(userId).getFriendList().remove(removingFriendId);
    }

    @Override
    public User updateUser(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    private int getNextId() {
        int currentMaxId = userMap.keySet().stream().mapToInt(id -> id).max().orElse(0);
        return ++currentMaxId;
    }

    @Override
    public boolean addFriend(int userId, int friendId) {
        User currentUser = userMap.get(userId);
        if (currentUser.getFriendList().contains(friendId)) {
            throw new ValidationException("Пользователи уже являются друзьями");
        }
        currentUser.addFriend(friendId);
        userMap.get(friendId).addFriend(userId);
        return true;
    }

    @Override
    public Set<Integer> getUserFriendList(int userId) {
        return userMap.get(userId).getFriendList();
    }
}
