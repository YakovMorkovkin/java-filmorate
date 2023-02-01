package ru.yandex.practicum.filmorate.dao;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Like;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Like> getAllLikes() {
        log.info("PF-8 Получение всех Like всех фильмов");
        String sql = "SELECT * FROM FILM_LIKES ORDER BY LIKED_BY";
        return jdbcTemplate.query(sql, (rs, rowNum) -> Like.builder()
                .filmId(rs.getLong("FILM_ID"))
                .userId(rs.getLong("LIKED_BY"))
                .build());
    }
}