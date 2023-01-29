package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

@Service("UserServiceDb")
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserServiceDb implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final EventDBStorage eventDBStorage;

    @Override
    public void addToFriends(Integer userId, Integer friendId) {
        if (userDbStorage.getUserById(userId).isEmpty() || userDbStorage.getUserById(friendId).isEmpty()) {
            throw new NotFoundException("Пользователя с id: " + userId + " или с id: " + friendId + " не существует");
        } else {

            String sql = "INSERT INTO user_friends (user_id,friends_with,confirmation) " +
                    "VALUES (?,?, NVL2 " +
                    "((SELECT * FROM user_friends WHERE confirmation = TRUE AND user_id = ? AND friends_with = ?)" +
                    ",TRUE,FALSE))";

            jdbcTemplate.update(sql
                    , userId
                    , friendId
                    , friendId
                    , userId

            );
            eventDBStorage.addEventToUserFeed(userId, friendId, EventType.FRIEND, Operation.ADD);
        }
    }

    @Override
    public void removeFromFriends(Integer userId, Integer friendId) {
        if (userDbStorage.getUserById(userId).isPresent() && userDbStorage.getUserById(friendId).isPresent()) {

            String sql = "DELETE FROM user_friends WHERE user_id = ? AND friends_with = ?";

            jdbcTemplate.update(sql
                    , userId
                    , friendId
            );

            jdbcTemplate.update(sql
                    , friendId
                    , userId
            );
            eventDBStorage.addEventToUserFeed(userId, friendId, EventType.FRIEND, Operation.REMOVE);
        } else throw new NotFoundException("Пользователя с id: " + userId + " или с id: " + friendId + " не существует");
    }

    @Override
    public Set<User> getFriendsOfUser(Integer userId) {
        TreeSet<User> users;
        if (userDbStorage.getUserById(userId).isEmpty()) {
            throw new NotFoundException("Пользователя с id: " + userId + " не существует");
        } else {
            String sql = "SELECT * " +
                    "FROM users " +
                    "WHERE id IN (" +
                    "SELECT friends_with " +
                    "FROM user_friends " +
                    "WHERE user_id = ?) " +
                    "ORDER BY id ";
        users = new TreeSet<>(Comparator.comparing(User::getId));
        users.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> userDbStorage.makeUser(rs), userId));
    }
        return users;
    }

    @Override
    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE id IN (" +
                "SELECT friends_with " +
                "FROM user_friends " +
                "WHERE user_id = ? " +
                "AND friends_with IN (" +
                "SELECT friends_with " +
                "FROM user_friends " +
                "WHERE user_id = ?))";

        TreeSet<User> users = new TreeSet<>(Comparator.comparing(User::getId));
        users.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> userDbStorage.makeUser(rs), userId, otherUserId));
        return users;
    }
}

