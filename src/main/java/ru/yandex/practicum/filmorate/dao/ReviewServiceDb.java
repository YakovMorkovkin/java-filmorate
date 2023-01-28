package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ResourceException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.review.ReviewService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Component
@Slf4j
@RequiredArgsConstructor
@Primary
public class ReviewServiceDb implements ReviewService {

    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final FilmDbStorage filmDbStorage;

    @Override
    public Review create(Review review) {
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());
        checkReview(review);
        review.setUseful(0);
        String sql = "INSERT INTO REVIEWS (content, isPositive, user_id, film_id, useful, dateOfPublication) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[]{"ID"});
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            return ps;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        log.info("Отзыв добавлен: {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());
        checkReviewId(review.getReviewId());
        String sql = "UPDATE REVIEWS SET content = ?, isPositive = ?, dateOfPublication = ? " +
                "WHERE id = ?";

        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                Timestamp.valueOf(LocalDateTime.now()),
                review.getReviewId());

        log.info("Отзыв обновлен: {}", review);
        return getReviewById(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        checkReviewId(id);
        String sqlL = "delete from REVIEW_LIKES where review_id = ?";
        String sqlR = "delete from REVIEWS where id = ?";
        jdbcTemplate.update(sqlL, id);
        jdbcTemplate.update(sqlR, id);
        log.info("Отзыв с id {} удален", id);
    }

    @Override
    public Review getReviewById(int id) {
        log.info("Получение отзыва с id %d", id);
        String sql = "select * from reviews " +
                "where id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeReview(rs), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Отзыв с id %d не найден", id));
        }
    }

    @Override
    public List<Review> getAll(int count) {
        log.info("Получение списка отзывов в количестве {}", count);
        String sql = "select * from reviews " +
                "order by useful DESC LIMIT " + count;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public List<Review> getAllReviewByFilm(int filmId, int count) {
        log.info("Получение списка отзывов в количестве {}", count);
        String sql = "select * from reviews " +
                "where film_id = " + filmId +
                "order by useful DESC LIMIT " + count;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public void addLike(int id, int userId) {
        checkReviewId(id);
        String sql = "INSERT INTO REVIEW_LIKES (review_id, user_id, islike) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, true);
        calcUseful(true, id);
    }

    @Override
    public void addDislike(int id, int userId) {
        checkReviewId(id);
        String sql = "INSERT INTO REVIEW_LIKES (review_id, user_id, islike) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, false);
        calcUseful(false, id);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        checkReviewId(id);
        String sql = "delete from REVIEW_LIKES where review_id = ? and user_id = ? and islike = ?";
        jdbcTemplate.update(sql, id, userId, false);
        calcUseful(false, id);
    }

    @Override
    public void deleteLike(int id, int userId) {
        checkReviewId(id);
        String sql = "delete from REVIEW_LIKES where review_id = ? and user_id = ? and islike = ?";
        jdbcTemplate.update(sql, id, userId, true);
        calcUseful(true, id);
    }

    private Review makeReview(ResultSet rs) throws SQLException {
        return Review.builder()
                .reviewId(rs.getInt("id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("isPositive"))
                .userId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .useful(rs.getInt("useful"))
                .dateOfPublication(rs.getTimestamp("dateOfPublication").toLocalDateTime())
                .build();
    }

    private void calcUseful (boolean isLike, int reviewId) {
        String sql = "UPDATE REVIEWS SET useful = ? " +
                "WHERE id = ?";
        int newUseful;
        int oldUseful = getReviewById(reviewId).getUseful();
        if (isLike) {
            newUseful = oldUseful + 1;
        } else {
            newUseful = oldUseful - 1;
        }
        jdbcTemplate.update(sql,
                newUseful,
                reviewId);
    }

    private void checkReviewId(int id) {
        if (getReviewById(id) == null) {
            throw new NotFoundException("Отзыв с таким id не найден.");
        }
    }

    private void checkUserId(int id) {
        if (userDbStorage.getUserById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с таким id не найден.");
        }
    }

    private void checkFilmId(int id) {
        if (filmDbStorage.getFilmById(id).isEmpty()) {
            throw new NotFoundException("Фильм с таким id не найден.");
        }
    }

    private void checkReview(Review review) {
        if (review.getContent() == null) {
            throw new ResourceException(HttpStatus.INTERNAL_SERVER_ERROR, "Отзыв не может быть пустым.");
        }
    }
}
