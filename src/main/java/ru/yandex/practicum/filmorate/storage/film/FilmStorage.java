package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

@Component
public interface FilmStorage {
    List<Film> getAllFilms();

    void deleteFilmById(int id);

    Optional<Film> getFilmById(int id);

    Film createFilm(Film film);

    Film updateFilm(Film film);
}
