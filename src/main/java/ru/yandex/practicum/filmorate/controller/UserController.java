package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Autowired
    private final HashMap<Integer,User> users = new HashMap<>();
    private int userId;

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("Количество пользователей в текущий момент: {}",users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        if (isExist(user,"email")) {
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

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (isExist(user,"id")) {
            users.remove(user.getId());
            users.put(user.getId(), user);
        } else throw new ValidationException("Пользователя с id: " + user.getId()+ " не существует");
        log.debug("Обновлены данные пользователя: {}", user);
        return users.get(user.getId());
    }

    boolean isExist(User user, String flag) {
        boolean isExist = false;
        switch(flag) {
            case"email":
            for (User u : users.values()) {
                if (u.getEmail().equals(user.getEmail())) {
                    isExist = true;
                    break;
                }
            }
            case"id":
            for (User u : users.values()) {
                if (u.getId() == user.getId()) {
                    isExist = true;
                    break;
                }
            }
        }
        return isExist;
    }


    int userIdGenerator(){
        return ++userId;
    }
}
