package ru.yandex.practicum.filmorate.storage.event;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;

import java.util.List;

@Component
public interface EventStorage {

    List<Event> getFeed(int id);
    void addEventToUserFeed(int userId, int entityId, EventType eventType, Operation operation);
}
