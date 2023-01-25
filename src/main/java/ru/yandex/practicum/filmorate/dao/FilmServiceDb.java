package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service("FilmServiceDb")
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmServiceDb implements FilmService {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;
    @Override
    public void addLike(Integer userId, Integer filmId) {
        if(userDbStorage.getUserById(userId).isPresent() &&filmDbStorage.getFilmById(filmId).isPresent()) {
            String sql = "INSERT INTO film_likes (film_id,liked_by) VALUES (?,?)";

            jdbcTemplate.update(sql
                    , filmId
                    , userId
            );
        } else throw new NotFoundException("Данные ошибочны.");
    }

    @Override
    public void removeLike(Integer userId, Integer filmId) {
        if(userDbStorage.getUserById(userId).isPresent() &&filmDbStorage.getFilmById(filmId).isPresent()) {
        String sql = "DELETE FROM film_likes WHERE film_id = ? AND liked_by = ?";

        jdbcTemplate.update(sql
                ,filmId
                ,userId
        );
        } else throw new NotFoundException("Данные ошибочны.");
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
        if(!jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), count).isEmpty()) {
            result = new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), count));
        } else {
            String sql1 = "SELECT * " +
                    "FROM films " +
                    "INNER JOIN mpa ON films.mpa = mpa.id " +
                    "LIMIT(?)";
            result = new HashSet<>(jdbcTemplate.query(sql1, (rs, rowNum) -> filmDbStorage.makeFilm(rs), count));
        }
        return result;
    }

    public Set<Genre> getAllGenres(){
        String sql = "SELECT * " +
                "FROM genre ";
        TreeSet<Genre> genreSet = new TreeSet<>(Comparator.comparing(Genre::getId));
        genreSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs)));
        return genreSet;
    }

    public Optional<Genre> getGenreById(int id){
        String sql = "SELECT * " +
                "FROM genre " +
                "WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRows.next()) {
            Genre genre = new Genre();
            genre.setId(filmRows.getInt("id"));
            genre.setName(filmRows.getString("genre_name"));
            return Optional.of(genre);
    } else {
            throw new NotFoundException("Жанра с id - " + id + "нет в базе.");
    }
    }
    public Set<Mpa> getAllMpa(){
        String sql = "SELECT * " +
                "FROM mpa ";
        TreeSet<Mpa> mpaSet = new TreeSet<>(Comparator.comparing(Mpa::getId));
        mpaSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs)));
        return mpaSet;
    }

    public Optional<Mpa> getMpaById(int id){
        String sql = "SELECT * " +
                "FROM mpa " +
                "WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRows.next()) {
            Mpa mpa = new Mpa();
            mpa.setId(filmRows.getInt("id"));
            mpa.setName(filmRows.getString("mpa_name"));
            return Optional.of(mpa);
        } else {
            throw new NotFoundException("Рейтинга с id - " + id + "нет в базе.");
        }
    }

    @Override
    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmDbStorage.getAllFilms().stream()
                .filter(x -> x.getLikes().contains((long) userId))
                .filter(x -> x.getLikes().contains((long) friendId))
                .sorted(Comparator.comparing(x -> (-1) * x.getLikes().size()))
                .collect(Collectors.toList());
    }

    Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("mpa_name"));
        return mpa;
    }

    Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }
}
