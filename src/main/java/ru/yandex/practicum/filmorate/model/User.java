package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.validator.NoBlankInside;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import java.util.Set;

@Data
@Component("user")
public class User {
    private int id;
    private Set<Long> friends;
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

    public void addFriend(Integer userId) {
            friends.add(Long.valueOf(userId));
    }

    public void removeFriend(Integer userId) {
        if(friends.contains(Long.valueOf(userId))) {
            friends.remove(Long.valueOf(userId));
        } else throw new NotFoundException("Пользователя с id-"
                + userId + " нет в друзьях у пользователя с с id-" + getId());
    }
}
