package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    @Autowired
    public UserService(InMemoryUserStorage inMemoryUserStorage) {
        this.inMemoryUserStorage = inMemoryUserStorage;
    }

    public void addToFriends(Integer userId, Integer friendId) {
        if (inMemoryUserStorage.getUsers().containsKey(userId)
                && inMemoryUserStorage.getUsers().containsKey(friendId)) {
            inMemoryUserStorage.getUsers().get(userId).getFriends().add(Long.valueOf(friendId));
            inMemoryUserStorage.getUsers().get(friendId).getFriends().add(Long.valueOf(userId));
            log.debug("Пользователи с id-{} и id-{} добавлены друг другу в друзья", userId, friendId);

        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    public void removeFromFriends(Integer userId, Integer friendId) {
        inMemoryUserStorage.getUsers().get(userId).getFriends().remove(Long.valueOf(friendId));
        inMemoryUserStorage.getUsers().get(friendId).getFriends().remove(Long.valueOf(userId));
    }

    public Set<User> getFriendsOfUser(Integer userId) {
        Set<Long> friendsIds = inMemoryUserStorage.getUsers().get(userId).getFriends();
        Set<User> friends = new HashSet<>();
        for (Long id : friendsIds) {
            friends.add(inMemoryUserStorage.getUsers().get(id.intValue()));
        }
        log.debug("Id друзей пользователя с id-{} : {}", userId, friends);
        return friends;
    }

    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        Set<Long> userFriends = inMemoryUserStorage.getUsers().get(userId).getFriends();
        Set<Long> otherUserFriends = inMemoryUserStorage.getUsers().get(otherUserId).getFriends();
        Set<Long> commonSet = new HashSet<>();
        if (userFriends != null && otherUserFriends != null) {
            commonSet = userFriends.stream()
                    .filter(otherUserFriends::contains)
                    .collect(Collectors.toSet());
        }
        Set<User> commonFriends = new HashSet<>();
        for (Long id : commonSet) {
            commonFriends.add(inMemoryUserStorage.getUsers().get(id.intValue()));
        }
        return commonFriends;
    }
}
