package ru.yandex.practicum.filmorate.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;
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
            eventDBStorage.addEventToUserFeed(userId, filmId, EventType.LIKE, Operation.ADD);
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
            eventDBStorage.addEventToUserFeed(userId, filmId, EventType.LIKE, Operation.REMOVE);
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

    public Set<Film> getPopularFilmsByGenreOrYear(Integer genreId, Integer year, Integer count) {
        String sql = "SELECT f.id AS id, f.name, f.description, f.release_date, f.duration, f.mpa, m.id, m.mpa_name " +
                "FROM films f " +
                "LEFT JOIN film_likes fl on f.id = fl.film_id " +
                "INNER JOIN mpa m on f.mpa = m.id " +
                "INNER JOIN films_genre fg ON f.id = fg.film_id " +
                "WHERE fg.genre_id = ? OR EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                "GROUP BY f.id " +
                "ORDER BY COUNT(fl.liked_by) " +
                "LIMIT ?";

        List<Film> popularFilms = jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), genreId, year, count);

        if (genreId != null && year != null) {
            return popularFilms.stream()
                    .filter(f -> !f.getGenres().isEmpty() && f.getReleaseDate().getYear() == year)
                    .collect(Collectors.toSet());
        }

        return new HashSet<>(popularFilms);
    }

    public Set<Genre> getAllGenres(){
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
            throw new NotFoundException("Жанра с id -" + id + " нет в базе.");
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
            throw new NotFoundException("Рейтинга с id -" + id + " нет в базе.");
        }
    }

    @Override
    public Set<Film> getSortedFilmsByDirectorId(int directorId, String sortBy) {
        Set<Film> result = new HashSet<>();
        String sortByYear = "ORDER BY (EXTRACT(YEAR FROM CAST(f.release_date AS date)))";
        String sortByLikes = "ORDER BY COUNT(fl.liked_by) DESC";
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
                "LEFT OUTER JOIN film_likes AS fl ON fd.film_id = fl.film_id " +
                "WHERE fd.director_id = ? " +
                "GROUP BY f.id ";
        if (!jdbcTemplate.query(sql, (rs, rowNum) -> filmDbStorage.makeFilm(rs), directorId).isEmpty()) {
            if (sortBy.equals("year")) {
                result = new LinkedHashSet<>(jdbcTemplate.query(sql + sortByYear,
                        (rs, rowNum) -> filmDbStorage.makeFilm(rs), directorId));
            } else if (sortBy.equals("likes"))
                result = new LinkedHashSet<>(jdbcTemplate.query(sql + sortByLikes,
                        (rs, rowNum) -> filmDbStorage.makeFilm(rs), directorId));
        } else throw new NotFoundException("Не найдено фильмов режиссера с id  = " + directorId);
        return result;
    }

    public Collection<Film> getCommonFilms(Integer userId, Integer friendId) {
        return filmDbStorage.getAllFilms().stream()
                .filter(x -> x.getLikes().contains((long) userId))
                .filter(x -> x.getLikes().contains((long) friendId))
                .sorted(Comparator.comparing(x -> (-1) * x.getLikes().size()))
                .collect(Collectors.toList());
    }
    
    @Override
    public Collection<Film> searchFilms(String query, String by) {
        Set<Film> result = new HashSet<>();
        final String sql = "SELECT f.id " +
                "           ,f.name " +
                "           ,f.description " +
                "           ,f.release_date " +
                "           ,f.duration " +
                "           ,f.mpa " +
                "           ,mpa.mpa_name " +
                "           ,COUNT(fl.liked_by) as count " +
                "FROM films AS f " +
                "LEFT OUTER JOIN mpa ON f.mpa = mpa.id " +
                "LEFT OUTER JOIN films_director AS fd ON f.id = fd.film_id " +
                "LEFT OUTER JOIN directors AS d ON fd.director_id = d.id " +
                "LEFT OUTER JOIN film_likes AS fl ON fd.film_id = fl.film_id ";

        final String byTitle = "WHERE LOWER(f.name) LIKE LOWER(CONCAT('%', ?, '%')) GROUP BY f.id ";
        final String byDirector = "WHERE LOWER(d.director_name) LIKE LOWER(CONCAT('%', ?, '%')) GROUP BY f.id ";
        final String order = "ORDER BY COUNT DESC";

        switch (by) {
            case "title":
                String sqlByTitle = sql + byTitle + order;
                result = new LinkedHashSet<>(jdbcTemplate.query(sqlByTitle,
                        (rs, rowNum) -> filmDbStorage.makeFilm(rs), query));
                break;
            case "director":
                String sqlByDirector = sql + byDirector + order;
                result = new LinkedHashSet<>(jdbcTemplate.query(sqlByDirector,
                        (rs, rowNum) -> filmDbStorage.makeFilm(rs), query));
                break;
            case "title,director":
            case "director,title":
                String sqlByTitleByDirector = sql + byTitle + " UNION ALL " + sql + byDirector + order;
                result = new LinkedHashSet<>(jdbcTemplate.query(sqlByTitleByDirector,
                        (rs, rowNum) -> filmDbStorage.makeFilm(rs), query, query));
                break;
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
        if (!jdbcTemplate.query(sql, (rs, rowNum) -> makeDirector(rs), id).isEmpty()) {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeDirector(rs), id));
        } else {
            throw new NotFoundException("Режиссера с id -" + id + " нет в базе.");
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