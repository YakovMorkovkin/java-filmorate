package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class Event implements Serializable {
    private Long timestamp;
    private int eventId;
    @NotEmpty(message = "Идентификатор пользователя не может быть пустым.")
    private int userId;
    @NotEmpty(message = "Идентификатор сущности не может быть пустым.")
    private int entityId;
    @NotEmpty(message = "Тип события не может быть пустым.")
    private EventType eventType;
    @NotEmpty(message = "Тип операции не может быть пустым.")
    private Operation operation;
}