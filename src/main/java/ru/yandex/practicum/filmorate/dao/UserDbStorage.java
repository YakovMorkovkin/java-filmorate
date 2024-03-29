package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("UserDbStorage")
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public Optional<User> getUserById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        Optional<User> result;
        try {
            result = Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), id));
        } catch (EmptyResultDataAccessException exp) {
            throw new NotFoundException("Пользователь с id - " + id + " не найден.");
        }
        return result;
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            if (user.getName().isEmpty() || user.getName().isBlank()) {
                stmt.setString(3, user.getLogin());
            } else {
                stmt.setString(3, user.getName());
            }
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);

        return getUserById(Objects.requireNonNull(keyHolder.getKey()).intValue()).orElse(null);
    }

    @Override
    public void deleteUserById(int id) {
        log.info(" PF-4 Удаление пользователя с id {}", id);
        //check User is present
        getUserById(id);
        String sql1 = "DELETE from USERS where ID=?";
        jdbcTemplate.update(sql1, id);
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? WHERE id = ?";
        jdbcTemplate.update(sql
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId()
        );
        if(getUserById(user.getId()).isEmpty()) {
            throw new NotFoundException("Пользователя с id: " + user.getId() + " не существует");
        } else return getUserById(user.getId()).orElse(null);
    }

    private Set<Long> getFriends(Integer userId) {
        String sql = "SELECT * FROM user_friends WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friends_with"), userId));
    }

    protected User makeUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setFriends(getFriends(rs.getInt("id")));
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setLogin(rs.getString("login"));
        user.setName(rs.getString("name"));
        user.setBirthday(rs.getDate("birthday").toLocalDate());
        return user;
    }
}