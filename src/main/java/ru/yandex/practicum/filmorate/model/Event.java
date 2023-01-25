package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.Instant;

@Data
public class Event implements Serializable {
    private Instant timestamp;
    private int eventId;
    @NotEmpty(message = "������������� ������������ �� ����� ���� ������.")
    private int userId;
    @NotEmpty(message = "������������� �������� �� ����� ���� ������.")
    private int entityId;
    @NotEmpty(message = "��� ������� �� ����� ���� ������.")
    private EventType eventType;
    @NotEmpty(message = "��� �������� �� ����� ���� ������.")
    private Operation operation;
}
