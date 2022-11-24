package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotEmpty(message = "Электронная почта не может быть пустой.")
    @Email(message = "Строка должна быть правильно сформированным адресом электронной почты.")
    private String email;
    @NotEmpty(message = "Логин не может быть пустым.")
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    private LocalDate birthday;
}
