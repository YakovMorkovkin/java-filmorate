package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
public class Mpa implements Serializable {
    private int id;
    private String name;

}
