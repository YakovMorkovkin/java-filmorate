package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer,User> users;
    private Integer userId = 0;
    @Autowired
    public InMemoryUserStorage(HashMap<Integer, User> users) {
        this.users = users;
    }

    @Override
    public List<User> getAllUsers() {
        log.debug("Количество пользователей в текущий момент: {}", users.size());
        return new ArrayList<>(users.values());
    }
    @Override
    public User getUserById(int id) {
            return users.get(id);
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
        log.debug("Новый пользователь: {}", user);
        return users.get(user.getId());
    }

    @Override
    public User updateUser(User user) {
        if (isExistById(user)) {
            users.remove(user.getId());
            users.put(user.getId(), user);
        } else throw new ValidationException("Пользователя с id: " + user.getId()+ " не существует");
        log.debug("Обновлены данные пользователя: {}", user);
        return users.get(user.getId());
    }

    private boolean isExistByEmail(User user) {
        boolean isExist = false;
        for (User u : users.values()) {
            if (u.getEmail().equals(user.getEmail())) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    private boolean isExistById(User user) {
        boolean isExist = false;
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

    public HashMap<Integer, User> getUsers() {
        return users;
    }
}
