package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
public interface FilmService {
    void addLike(Integer userId, Integer filmId);

    void removeLike(Integer userId, Integer filmId);

    Set<Film> getCountOfTheBestFilms(Integer count);

    Set<Film> getPopularFilmsByGenreOrYear(Integer genreId, Integer year, Integer count);

    Set<Genre> getAllGenres();

    Optional<Genre> getGenreById(int id);

    Set<Mpa> getAllMpa();

    Optional<Mpa> getMpaById(int id);


    Set<Film> getSortedFilmsByDirectorId(int directorId, String sortBy);

    Set<Director> getAllDirectors();

    Optional<Director> getDirectorById(int id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void removeDirector(int id);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);

    Collection<Film> searchFilms(String query, String by);

}
