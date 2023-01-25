package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;

@Component("EventDBStorage")
@Slf4j
@RequiredArgsConstructor
public class EventDBStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeed(int id) {
        String sql = "SELECT * " +
                "FROM user_events " +
                "WHERE user_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeEvent(rs), id);

    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setTimestamp(Instant.ofEpochSecond(rs.getInt("time_of_event")));
        event.setEventId(rs.getInt("event_id"));
        event.setUserId(rs.getInt("user_id"));
        event.setEntityId(rs.getInt("entity_id"));
        event.setEventType(EventType.valueOf(rs.getString("event_type")));
        event.setOperation(Operation.valueOf(rs.getString("operation")));
        return event;
    }

    @Override
    public void addEventToUserFeed(int userId, int entityId, EventType eventType, Operation operation) {
        String sql = "INSERT INTO user_events (" +
                "time_of_event" +
                ", user_id" +
                ", entity_id" +
                ", event_type" +
                ", operation" +
                ") " +
                "VALUES (?,?,?,?,?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            stmt.setObject(1, Instant.now().toEpochMilli() / 1000);
            stmt.setInt(2, userId);
            stmt.setInt(3, entityId);
            stmt.setString(4, eventType.toString());
            stmt.setString(5, operation.toString());
            return stmt;
        });
    }
}
