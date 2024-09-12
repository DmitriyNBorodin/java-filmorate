package ru.yandex.practicum.filmorate.storage;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mappers.UserRowMapper;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper userRowMapper;
    private static final String GET_ALL_QUERY = "SELECT * FROM users";
    private static final String GET_BY_ID_QUERY = "SELECT * FROM users WHERE id = ?";
    private static final String ADD_USER_QUERY = "INSERT INTO users (email, login, " +
            "name, birthdate) VALUES (?, ?, ?, ?)";
    private static final String DELETE_QUERY = "DELETE FROM users WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthdate = ? WHERE id = ?";
    private static final String CHECK_QUERY = "SELECT COUNT (login) FROM users WHERE id = ?";
    private static final String REMOVE_FRIENDSHIP_QUERY = "DELETE FROM user_friends WHERE user_id = ? AND friend_id = ?";
    private static final String ADD_FRIEND_QUERY = "INSERT INTO user_friends (user_id, friend_id, request_status) VALUES (?, ?, 1)";
    private static final String GET_FRIEND_LIST_QUERY = "SELECT friend_id FROM user_friends WHERE user_id = ?";

    @Override
    public List<User> getUsers() {
        return jdbcTemplate.query(GET_ALL_QUERY, userRowMapper);
    }

    @Override
    public Optional<User> getUserById(int id) {
        return Optional.ofNullable(jdbcTemplate.queryForObject(GET_BY_ID_QUERY, userRowMapper, id));
    }

    @Override
    public User addUser(User user) {
        int newUserId = insert(ADD_USER_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday());
        user.setId(newUserId);
        return user;
    }

    @Override
    public boolean deleteUser(User user) {
        return jdbcTemplate.update(DELETE_QUERY, user.getId()) > 0;
    }

    @Override
    public User updateUser(User user) {
        jdbcTemplate.update(UPDATE_QUERY, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public boolean checkUser(int userId) {
        Optional<Integer> userCheck = Optional.ofNullable(jdbcTemplate.queryForObject(CHECK_QUERY, Integer.class, userId));
        if (userCheck.isPresent() && userCheck.get() != 0) {
            return true;
        } else {
            throw new NotFoundException("Пользователь с id " + userId + " не найден!");
        }
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        jdbcTemplate.update(REMOVE_FRIENDSHIP_QUERY, userId, friendId);
    }

    public boolean addFriend(int userId, int friendId) {
        if (checkUser(userId) && checkUser(friendId)) {
            jdbcTemplate.update(ADD_FRIEND_QUERY, userId, friendId);
            return true;
        }
        return false;
    }

    protected int insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);
        int id = keyHolder.getKeyAs(Integer.class);
        if (id != 0) {
            return id;
        } else {
            throw new RuntimeException("Не удалось сохранить данные");
        }
    }

    @Override
    public Set<Integer> getUserFriendList(int userId) {
        List<Integer> friendList = jdbcTemplate.queryForList(GET_FRIEND_LIST_QUERY, Integer.class, userId);
        return new HashSet<>(friendList);
    }
}
