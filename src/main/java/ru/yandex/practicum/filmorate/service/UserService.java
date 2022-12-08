package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final InMemoryUserStorage inMemoryUserStorage;

    public void addToFriends(Integer userId, Integer friendId) {
        if (getUsers().containsKey(userId) && getUsers().containsKey(friendId)) {
            getUser(userId).addFriend(friendId);
            getUser(friendId).addFriend(userId);
            log.info("Пользователи с id-{} и id-{} добавлены друг другу в друзья", userId, friendId);
        } else {
            throw new NotFoundException("Один или оба пользователя не найдены в базе");
        }
    }

    public void removeFromFriends(Integer userId, Integer friendId) {
        if (getUsers().containsKey(userId) && getUsers().containsKey(friendId)) {
            getUser(userId).removeFriend(friendId);
            getUser(friendId).removeFriend(userId);
            log.info("Пользователи с id-{} и id-{} удалены друг у друга из друзей", userId, friendId);
        } else {
            throw new NotFoundException("Один или оба пользователя не найдены в базе");
        }
    }

    public Set<User> getFriendsOfUser(Integer userId) {
        Set<Long> friendsIds = getUserFriends(userId);
        Set<User> friends = new HashSet<>();
        for (Long id : friendsIds) {
            friends.add(getUser(id.intValue()));
        }
        log.info("Id друзей пользователя с id-{} : {}", userId, friends);
        return friends;
    }

    public Set<User> getCommonFriends(Integer userId, Integer otherUserId) {
        Set<Long> userFriends = getUserFriends(userId);
        Set<Long> otherUserFriends = getUserFriends(otherUserId);
        Set<Long> commonSet = new HashSet<>();
        if (userFriends != null && otherUserFriends != null
                && !userFriends.isEmpty() && !otherUserFriends.isEmpty()) {
            commonSet = userFriends.stream()
                    .filter(otherUserFriends::contains)
                    .collect(Collectors.toSet());
        }
        Set<User> commonFriends = new HashSet<>();
        for (Long id : commonSet) {
            commonFriends.add(getUser(id.intValue()));
        }
        return commonFriends;
    }

    private User getUser(Integer userId){
        return inMemoryUserStorage.getUsers().get(userId);
    }

    private Map<Integer, User> getUsers(){
        return inMemoryUserStorage.getUsers();
    }

    private Set<Long> getUserFriends(Integer userId){
        return inMemoryUserStorage.getUsers().get(userId).getFriends();
    }
}
