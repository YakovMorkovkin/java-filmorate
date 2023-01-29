package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        reviewService.delete(id);
    }

    @GetMapping
    public List<Review> findAll(@RequestParam(defaultValue = "0", required = false) int filmId,
                                @RequestParam(defaultValue = "10", required = false) int count) {
        if (filmId == 0) {
            return reviewService.getAll(count);
        } else {
            return reviewService.getAllReviewByFilm(filmId, count);
        }
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable("id") int id) {
        return reviewService.getReviewById(id);
    }

    @PutMapping("{id}/like/{userId}")
    public void addLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.addLike(id, userId);
    }

    @PutMapping("{id}/dislike/{userId}")
    public void addDislike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.deleteLike(id, userId);
    }

    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable("id") int id, @PathVariable("userId") int userId) {
        reviewService.deleteDislike(id, userId);
    }
}
