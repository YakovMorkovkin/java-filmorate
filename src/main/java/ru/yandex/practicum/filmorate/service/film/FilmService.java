package ru.yandex.practicum.filmorate.service.film;

import org.springframework.stereotype.Service;
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

    Set<Genre> getAllGenres();

    Optional<Genre> getGenreById(int id);

    Set<Mpa> getAllMpa();

    Optional<Mpa> getMpaById(int id);

    Collection<Film> getCommonFilms(Integer userId, Integer friendId);
}
