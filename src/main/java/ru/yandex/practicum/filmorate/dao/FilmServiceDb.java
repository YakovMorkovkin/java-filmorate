package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Comparator.comparing;

@Service("FilmServiceDb")
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmServiceDb implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    private final EventDBStorage eventDBStorage;

    @Override
    public void addLike(Integer userId, Integer filmId) {
        if (userDbStorage.getUserById(userId).isPresent() && filmDbStorage.getFilmById(filmId).isPresent()) {
            String sql = "INSERT INTO film_likes (film_id,liked_by) VALUES (?,?)";
            jdbcTemplate.update(sql, filmId, userId);
        } else throw new NotFoundException("Данные ошибочны.");
        eventDBStorage.addEventToUserFeed(userId, filmId, EventType.LIKE, Operation.ADD);
    }

    @Override
    public void removeLike(Integer userId, Integer filmId) {
        if (userDbStorage.getUserById(userId).isPresent() && filmDbStorage.getFilmById(filmId).isPresent()) {
            String sql = "DELETE FROM film_likes WHERE film_id = ? AND liked_by = ?";

            jdbcTemplate.update(sql
                    , filmId
                    , userId
            );
        } else throw new NotFoundException("Данные ошибочны.");
        eventDBStorage.addEventToUserFeed(userId, filmId, EventType.LIKE, Operation.REMOVE);
    }

    @Override
    public Set<Film> getCountOfTheBestFilms(Integer count) {
        Set<Film> result;
        String sql = "SELECT * " +
                "FROM films " +
                "INNER JOIN mpa ON films.mpa = mpa.id " +
                "WHERE films.id IN (" +
                "SELECT film_id " +
                "FROM film_likes " +
                "GROUP BY film_id " +
                "ORDER BY COUNT(liked_by) DESC " +
                "LIMIT(?)" +
                ")";
        if (!jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), count).isEmpty()) {
            result = new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), count));
        } else {
            String sql1 = "SELECT * " +
                    "FROM films " +
                    "INNER JOIN mpa ON films.mpa = mpa.id " +
                    "LIMIT(?)";
            result = new LinkedHashSet<>(jdbcTemplate.query(sql1, (rs, rowNum) -> filmDbStorage.makeFilm(rs), count));
        }
        return result;
    }

    public Set<Genre> getAllGenres() {
        String sql = "SELECT * " +
                "FROM genre ";
        TreeSet<Genre> genreSet = new TreeSet<>(comparing(Genre::getId));
        genreSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs)));
        return genreSet;
    }

    Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }

    public Optional<Genre> getGenreById(int id) {
        String sql = "SELECT * " +
                "FROM genre " +
                "WHERE id = ?";
        if (!jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), id).isEmpty()) {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id));
        } else {
            throw new NotFoundException("Жанра с id - " + id + " не найден.");
        }
    }

    public Set<Mpa> getAllMpa() {
        String sql = "SELECT * " +
                "FROM mpa ";
        TreeSet<Mpa> mpaSet = new TreeSet<>(comparing(Mpa::getId));
        mpaSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs)));
        return mpaSet;
    }

    Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("mpa_name"));
        return mpa;
    }

    public Optional<Mpa> getMpaById(int id) {
        String sql = "SELECT * " +
                "FROM mpa " +
                "WHERE id = ?";
        if (!jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id).isEmpty()) {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id));
        } else {
            throw new NotFoundException("Рейтинга с id - " + id + " не найден.");
        }
    }
}