package ru.yandex.practicum.filmorate.service.review;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Component
public interface ReviewService {

    Review create(Review review);

    Review update(Review review);

    void delete(int id);

    Review getReviewById(int id);

    List<Review> getAll(int count);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Review> getAllReviewByFilm(int filmId, int count);

    void addDislike(int id, int userId);

    void deleteDislike(int id, int userId);
}
