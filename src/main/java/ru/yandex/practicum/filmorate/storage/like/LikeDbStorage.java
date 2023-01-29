package ru.yandex.practicum.filmorate.storage.like;


import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;

import java.util.List;

@Slf4j
@Component

public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    public LikeDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Like> getAllFilms() {
        String sql = "SELECT * FROM FILM_LIKES ORDER BY LIKED_BY";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Like.builder()
                .filmId(rs.getLong("FILM_ID"))
                .userId(rs.getLong("LIKED_BY"))
                .build());
    }
}