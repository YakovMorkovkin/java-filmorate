package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component("InMemory")
@Slf4j
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer,User> users;
    private Integer userId = 0;

    @Override
    public List<User> getAllUsers() {
        log.info("Количество пользователей в текущий момент: {}", users.size());
        return new ArrayList<>(users.values());
    }
    @Override
    public Optional<User> getUserById(int id) {
        if(users.get(id) != null) {
            User user = users.get(id);
            return Optional.of(user);

        } else return Optional.empty();
    }

    @Override
    public User createUser(User user) {
        if (isExistByEmail(user)) {
            throw new ValidationException("Пользователь с id: " + user.getEmail() + " уже существует.");
        } else {
            user.setId(userIdGenerator());
            if (user.getName() == null || user.getName().isBlank() || user.getName().isEmpty()){
                user.setName(user.getLogin());
            }
            users.put(user.getId(),user);
        }
        log.info("Новый пользователь: {}", user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
    }

    @Override
    public User updateUser(User user) {
        if (isExistById(user)) {
            users.remove(user.getId());
            users.put(user.getId(), user);
        } else throw new ValidationException("Пользователя с id: " + user.getId()+ " не существует");
        log.info("Обновлены данные пользователя: {}", user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUserById(int id) {
        users.remove(id);
    }

    private boolean isExistByEmail(User user) {
        var isExist = false;
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    private boolean isExistById(User user) {
        var isExist = false;
        for (User u : users.values()) {
            if (u.getId() == user.getId()) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    private int userIdGenerator(){
        return ++userId;
    }

    public Map<Integer, User> getUsers() {
        return users;
    }
}
