package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
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
    private final EventDBStorage eventDBStorage;

    @Override
    public Review create(Review review) {
        log.info("PF-3 Создание нового отзыва на фильм с id-{} от пользователя с id-{} ",
                review.getFilmId(), review.getUserId());
        checkUserId(review.getUserId());
        checkFilmId(review.getFilmId());
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
        eventDBStorage.addEventToUserFeed(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.ADD);
        log.info("Отзыв добавлен: {}", review);
        return review;
    }

    @Override
    public Review update(Review review) {
        log.info("PF-3 Обновление отзыва на фильм с id-{} от пользователя с id-{} ",
                review.getFilmId(), review.getUserId());
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
        Review updatedReview = getReviewById(review.getReviewId());
        eventDBStorage.addEventToUserFeed(updatedReview.getUserId(), updatedReview.getReviewId(), EventType.REVIEW, Operation.UPDATE);
        return getReviewById(review.getReviewId());
    }

    @Override
    public void delete(int id) {
        log.info("PF-3 Удаление отзыва  с id-{}", id);
        checkReviewId(id);
        Review review = getReviewById(id);
        eventDBStorage.addEventToUserFeed(review.getUserId(), review.getReviewId(), EventType.REVIEW, Operation.REMOVE);
        String sqlL = "delete from REVIEW_LIKES where review_id = ?";
        String sqlR = "delete from REVIEWS where id = ?";
        jdbcTemplate.update(sqlL, id);
        jdbcTemplate.update(sqlR, id);
        log.info("Отзыв с id {} удален", id);
    }

    @Override
    public Review getReviewById(int id) {
        log.info("PF-3 Получение отзыва с id {}", id);
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
        log.info("PF-3 Получение списка отзывов в количестве {}", count);
        String sql = "select * from reviews " +
                "order by useful DESC LIMIT " + count;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public List<Review> getAllReviewByFilm(int filmId, int count) {
        log.info("PF-3 Получение всех отзывов на фильм с id - {}", filmId);
        String sql = "select * from reviews " +
                "where film_id = " + filmId +
                "order by useful DESC LIMIT " + count;
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeReview(rs));
    }

    @Override
    public void addLike(int id, int userId) {
        log.info("PF-3 Добавление Like отзыву с id - {} от пользователя с id - {}", id, userId);
        checkReviewId(id);
        String sql = "INSERT INTO REVIEW_LIKES (review_id, user_id, islike) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, true);
        calcUseful(true, id);
    }

    @Override
    public void addDislike(int id, int userId) {
        log.info("PF-3 Добавление Dislike отзыву с id - {} от пользователя с id - {}", id, userId);
        checkReviewId(id);
        String sql = "INSERT INTO REVIEW_LIKES (review_id, user_id, islike) " +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, id, userId, false);
        calcUseful(false, id);
    }

    @Override
    public void deleteDislike(int id, int userId) {
        log.info("PF-3 Удаление Dislike отзыву с id - {} от пользователя с id - {}", id, userId);
        checkReviewId(id);
        String sql = "delete from REVIEW_LIKES where review_id = ? and user_id = ? and islike = ?";
        jdbcTemplate.update(sql, id, userId, false);
        calcUseful(false, id);
    }

    @Override
    public void deleteLike(int id, int userId) {
        log.info("PF-3 Удаление Like отзыву с id - {} от пользователя с id - {}", id, userId);
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
        log.info("PF-3 Расчет рейтинга полезности отзыва с id - {}", reviewId);
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
            throw new NotFoundException("Отзыв с id - " + id + " не найден.");
        }
    }

    private void checkUserId(int id) {
        if (userDbStorage.getUserById(id).isEmpty()) {
            throw new NotFoundException("Пользователь с id - " + id + " не найден.");
        }
    }

    private void checkFilmId(int id) {
        if (filmDbStorage.getFilmById(id).isEmpty()) {
            throw new NotFoundException("Фильм с id - " + id + " не найден.");
        }
    }
}
