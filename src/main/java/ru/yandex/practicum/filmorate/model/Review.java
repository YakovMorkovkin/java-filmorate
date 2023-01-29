package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@AllArgsConstructor
public class Review {

    int reviewId;
    @NotNull
    String content;
    @NotNull
    Boolean isPositive;
    @NotNull
    Integer userId;
    @NotNull
    Integer filmId;
    int useful;
    LocalDateTime dateOfPublication;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return content.equals(review.content) && isPositive.equals(review.isPositive) && userId.equals(review.userId) && filmId.equals(review.filmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, isPositive, userId, filmId);
    }
}
