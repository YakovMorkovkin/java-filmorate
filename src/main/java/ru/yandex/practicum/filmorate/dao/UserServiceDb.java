package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Service("UserServiceDb")
@Slf4j
@RequiredArgsConstructor
@Primary
public class UserServiceDb implements UserService {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    @Override
    public void addToFriends(Integer userId, Integer friendId) {

        if(userDbStorage.getUserById(userId).isEmpty() || userDbStorage.getUserById(friendId).isEmpty()){
            throw new NotFoundException("Пользователя с id: " + userId +" или с id: " + friendId + " не существует");
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
        }
    }

    @Override
    public void removeFromFriends(Integer userId, Integer friendId) {
        String sql = "DELETE FROM user_friends WHERE user_id = ? AND friends_with = ?";

        jdbcTemplate.update(sql
                ,userId
                ,friendId
        );

        jdbcTemplate.update(sql
                ,friendId
                ,userId
        );
    }

    @Override
    public Set<User> getFriendsOfUser(Integer userId) {
        String sql = "SELECT * " +
                "FROM users " +
                "WHERE id IN (" +
                "SELECT friends_with " +
                "FROM user_friends " +
                "WHERE user_id = ?) " +
                "ORDER BY id ";
        TreeSet<User> users = new TreeSet<>(Comparator.comparing(User::getId));
        users.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId));
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
        users.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherUserId));
        return users;
    }

    private Set<Long> getFriends(Integer userId) {
        String sql = "SELECT * FROM user_friends WHERE user_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friends_with"), userId));
    }

    private User makeUser(ResultSet rs) throws SQLException {
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
