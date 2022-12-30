package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Genre implements Serializable {
    private int id;
    private String name;
}
