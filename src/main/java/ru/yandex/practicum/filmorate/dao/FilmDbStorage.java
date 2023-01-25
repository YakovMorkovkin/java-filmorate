package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component("FilmDbStorage")
@Slf4j
@RequiredArgsConstructor
@Primary
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> getAllFilms() {
        String sql = "SELECT * " +
                "FROM films AS f " +
                "INNER JOIN mpa ON f.mpa = mpa.id ";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Optional<Film> getFilmById(int id) {
        String sql = "SELECT * " +
                "FROM films AS f " +
                "INNER JOIN mpa ON f.mpa = mpa.id " +
                "WHERE f.id = ?";
        if (!jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id).isEmpty()) {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), id));
        } else {
            throw new NotFoundException("Фильм с идентификатором " + id + " не найден.");
        }
    }

    protected Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film();
        film.setLikes(getFilmLikes(rs.getInt("id")));
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("release_date").toLocalDate());
        film.setDuration(rs.getInt("duration"));
        film.setMpa(makeMpa(rs));
        film.setDirectors(getFilmDirectors(rs.getInt("id")));
        film.setGenres(getFilmGenres(rs.getInt("id")));
        return film;
    }

    Mpa makeMpa(ResultSet rs) throws SQLException {
        Mpa mpa = new Mpa();
        mpa.setId(rs.getInt("mpa"));
        mpa.setName(rs.getString("mpa_name"));
        return mpa;
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (name, description, release_date, duration, mpa) VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        int recordId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            jdbcTemplate.batchUpdate("MERGE INTO films_genre (film_id,genre_id) VALUES (?,?)"
                    , new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                            preparedStatement.setInt(1, recordId);
                            preparedStatement.setInt(2, new ArrayList<>(film.getGenres()).get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return film.getGenres().size();
                        }
                    });
        }

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            jdbcTemplate.batchUpdate("MERGE INTO films_director (film_id,director_id) VALUES (?,?) "
                    , new BatchPreparedStatementSetter() {
                        @Override
                        public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                            preparedStatement.setInt(1, recordId);
                            preparedStatement.setInt(2, new ArrayList<>(film.getDirectors()).get(i).getId());
                        }

                        @Override
                        public int getBatchSize() {
                            return film.getDirectors().size();
                        }
                    });
        }
        return getFilmById(recordId).orElse(null);
    }

    @Override
    public Film updateFilm(Film film) {
        if (getFilmById(film.getId()).orElse(null) == null) {
            throw new NotFoundException("Фильма с id - " + film.getId() + "нет в базе данных.");
        } else {
            String sql = "UPDATE films " +
                    "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
                    "WHERE id = ?";

            jdbcTemplate.update(sql
                    , film.getName()
                    , film.getDescription()
                    , film.getReleaseDate()
                    , film.getDuration()
                    , film.getMpa().getId()
                    , film.getId()

            );

            String sql1 = "DELETE FROM films_genre WHERE film_id = ?";

            jdbcTemplate.update(sql1, film.getId());

            String sql2 = "MERGE INTO films_genre (film_id,genre_id) VALUES (?,?) ";

            if (film.getGenres() != null && !film.getGenres().isEmpty()) {
                for (Genre g : film.getGenres()) {
                    jdbcTemplate.update(connection -> {
                        PreparedStatement stmt = connection.prepareStatement(sql2);
                        stmt.setInt(1, film.getId());
                        stmt.setInt(2, g.getId());
                        return stmt;
                    });
                }
            }

            String sql3 = "DELETE FROM films_director WHERE film_id = ?";

            jdbcTemplate.update(sql3, film.getId());

            String sql4 = "MERGE INTO films_director (film_id,director_id) VALUES (?,?) ";

            if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
                for (Director d : film.getDirectors()) {
                    jdbcTemplate.update(connection -> {
                        PreparedStatement stmt = connection.prepareStatement(sql4);
                        stmt.setInt(1, film.getId());
                        stmt.setInt(2, d.getId());
                        return stmt;
                    });
                }
            }
        }
        return getFilmById(film.getId()).orElse(null);
    }

    private Set<Long> getFilmLikes(Integer filmId) {
        String sql = "SELECT * FROM film_likes WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("liked_by"), filmId));
    }

    private Set<Genre> getFilmGenres(Integer filmId) {
        String sql = "SELECT * " +
                "FROM films_genre AS fg " +
                "INNER JOIN genre AS gen ON fg.genre_id = gen.id " +
                "WHERE film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId));
    }

    Genre makeGenre(ResultSet rs) throws SQLException {
        Genre genre = new Genre();
        genre.setId(rs.getInt("genre_id"));
        genre.setName(rs.getString("genre_name"));
        return genre;
    }

    private Set<Director> getFilmDirectors(Integer filmId) {
        String sql = "SELECT * " +
                "FROM films_director AS fd " +
                "INNER JOIN directors AS d ON fd.director_id = d.id " +
                "WHERE fd.film_id = ?";
        return new HashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), filmId));
    }

    private Director makeDirector(ResultSet rs) throws SQLException {
        Director director = new Director();
        director.setId(rs.getInt("director_id"));
        director.setName(rs.getString("director_name"));
        return director;
    }
}
