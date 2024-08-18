package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;
    private Set<Integer> friendList;

    public User(String email, String login, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.birthday = birthday;
        name = login;
        friendList = new HashSet<>();
    }

    public void addFriend(int friendId) {
        friendList.add(friendId);
    }

    public void removeFriend(int friendId) {
        friendList.remove(friendId);
    }
}
