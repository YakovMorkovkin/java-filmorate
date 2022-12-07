package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.Data;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.validator.ReleaseDate;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@Component
public class Film {
    private final Set<Long> likes = new HashSet<>();
    private int id;
    @NotEmpty(message = "Название не может быть пустым.")
    private String name;
    @NotNull
    @Size(max = 200, message = "Длина описания должна быть не более 200 символов.")
    private String description;
    @NotNull
    @ReleaseDate
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate releaseDate;
    @NotNull
    @Positive(message = "Длительность должна быть положительной.")
    private Integer duration;

    public void addLike(Integer userId) {
        likes.add(Long.valueOf(userId));
    }

    public void removeLike(Integer userId) {
        if(likes.contains(Long.valueOf(userId))) {
            likes.remove(Long.valueOf(userId));
        } else throw new NotFoundException("Пользователь с id-"
                + userId + " не ставил like фильму с id-" + getId());
    }
}
