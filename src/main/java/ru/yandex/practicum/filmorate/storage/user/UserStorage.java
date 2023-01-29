package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
@Component
public interface UserStorage {
    List<User> getAllUsers();
    Optional<User> getUserById(int id);
    User createUser(User user);

    void deleteUserById(int id);

    User updateUser(User user);
    void deleteUserById(int id);
}
