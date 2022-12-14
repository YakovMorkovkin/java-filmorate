package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;


    @GetMapping
    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        if (userStorage.getUserById(id).isEmpty()) {
            throw new NotFoundException("Пользователь не найден в базе");
        }
        User user = userStorage.getUserById(id).orElse(null);
        log.info("Пользователь с id-{}: {}", id, user);
        return user;
    }

    @GetMapping("/{id}/friends")
    public Set<User> getFriendsOfUser(@PathVariable int id) {
        return userService.getFriendsOfUser(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Set<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        Set<User> commonFriends = userService.getCommonFriends(id, otherId);
        log.info("Общие друзья пользователей с id-{} и id-{} : {}"
                , id, otherId, commonFriends);
        return commonFriends;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userStorage.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userStorage.updateUser(user);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addToFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.addToFriends(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFromFriends(@PathVariable int id, @PathVariable int friendId) {
        log.info("Пользователи с id-{} и id-{} удалены друг у друга из друзей", id, friendId);
        userService.removeFromFriends(id, friendId);
    }
}
