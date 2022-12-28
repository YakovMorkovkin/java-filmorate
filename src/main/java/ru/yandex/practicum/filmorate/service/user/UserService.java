package ru.yandex.practicum.filmorate.service.user;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;
@Service
public interface UserService {
    void addToFriends(Integer userId, Integer friendId);

    void removeFromFriends(Integer userId, Integer friendId);

    Set<User> getFriendsOfUser(Integer userId);

    Set<User> getCommonFriends(Integer userId, Integer otherUserId);
}
