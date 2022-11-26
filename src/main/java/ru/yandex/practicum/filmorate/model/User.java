package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.validator.NoBlankInside;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Component
public class User {
    private int id;
    @NotEmpty(message = "Электронная почта не может быть пустой.")
    @Email(message = "Строка должна быть правильно сформированным адресом электронной почты.")
    private String email;
    @NotEmpty(message = "Логин не может быть пустым.")
    @NoBlankInside
    private String login;
    private String name;
    @NotNull
    @PastOrPresent
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;
}
