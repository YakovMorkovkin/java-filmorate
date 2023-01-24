package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@Data
public class Director implements Serializable {
    private int id;
    @NotEmpty(message = "��� ��������� �� ����� ���� ������.")
    private String name;

}