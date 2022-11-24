package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

@Data
public class Film {
    public static final ChronoLocalDate DATE = LocalDate.of(1895, 12, 28);

    private int id;
    @NotEmpty(message = "Название не может быть пустым.")
    private String name;
    @NotNull
    @Size(max =200, message = "Длина описания должна быть не более 200 символов.")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @NotNull
    @Positive(message = "Длительность должна быть положительной.")
    private Integer duration;
}
