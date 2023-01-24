package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.util.Comparator.comparing;

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
        if (userDbStorage.getUserById(userId).isPresent() && filmDbStorage.getFilmById(filmId).isPresent()) {
            String sql = "INSERT INTO film_likes (film_id,liked_by) VALUES (?,?)";

            jdbcTemplate.update(sql
                    , filmId
                    , userId
            );
        } else throw new NotFoundException("Данные ошибочны.");
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
    public Set<Film> getSortedFilmsByDirectorId(int directorId, String sortBy) {
        Set<Film> result = new HashSet<>();
        String sortYear = "ORDER BY (EXTRACT(YEAR FROM CAST(f.release_date AS date))) DESC";
        String sortLikes = "ORDER BY COUNT(fl.liked_by) DESC";
        String sql = "SELECT f.id " +
                "           ,f.name " +
                "           ,f.description " +
                "           ,f.release_date " +
                "           ,f.duration " +
                "           ,f.mpa " +
                "           ,mpa.mpa_name " +
                "FROM films AS f " +
                "INNER JOIN films_director AS fd ON f.id = fd.film_id " +
                "INNER JOIN mpa ON f.mpa = mpa.id " +
                "INNER JOIN film_likes AS fl ON fd.film_id = fl.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id ";

        if (!jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), directorId).isEmpty()) {
            if (sortBy.equals("year")) {
                result = new LinkedHashSet<>(jdbcTemplate.query(sql + sortYear, (rs, rowNum) -> filmDbStorage.makeFilm(rs), directorId));
            } else if (sortBy.equals("likes"))
                result = new LinkedHashSet<>(jdbcTemplate.query(sql + sortLikes, (rs, rowNum) -> filmDbStorage.makeFilm(rs), directorId));
        }

        return result;
    }

    @Override
    public Set<Director> getAllDirectors() {
        String sql = "SELECT * " +
                "FROM directors ";
        TreeSet<Director> directorSet = new TreeSet<>(comparing(Director::getId));
        directorSet.addAll(jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs)));
        return directorSet;
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getInt("id"));
        director.setName(rs.getString("director_name"));
        return director;
    }

    @Override
    public Optional<Director> getDirectorById(int id) {
        String sql = "SELECT * " +
                "FROM directors " +
                "WHERE id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (filmRows.next()) {
            Director director = new Director();
            director.setId(filmRows.getInt("id"));
            director.setName(filmRows.getString("director_name"));
            return Optional.of(director);
        } else {
            throw new NotFoundException("Режиссера с id - " + id + "нет в базе.");
        }
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors (director_name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        int recordId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        return getDirectorById(recordId).orElse(null);
    }

    @Override
    public Director updateDirector(Director director) {

        if (getDirectorById(director.getId()).orElse(null) == null) {
            throw new NotFoundException("Режиссера с id - " + director.getId() + "нет в базе данных.");
        } else {
            String sql = "UPDATE directors " +
                    "SET director_name = ? " +
                    "WHERE id = ?";

            jdbcTemplate.update(sql
                    , director.getName()
                    , director.getId()

            );
            return getDirectorById(director.getId()).orElse(null);
        }
    }

    @Override
    public void removeDirector(int id) {
        String sql = "DELETE FROM directors WHERE id = ?";

        jdbcTemplate.update(sql, id);
    }
}
