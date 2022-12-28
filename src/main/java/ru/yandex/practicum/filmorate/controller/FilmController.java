package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.film.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    @GetMapping
    public List<Film>  getAllFilms() {
        log.info("Количество фильмов в базе: {}",filmStorage.getAllFilms().size());
        return filmStorage.getAllFilms();
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        if (filmStorage.getFilmById(id).isEmpty()) {
            throw new NotFoundException("Фильм не найден в базе");
        }
        log.info("Фильм с id-{}: {}", id, filmStorage.getFilmById(id));
        return filmStorage.getFilmById(id).orElse(null);
    }

    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam(defaultValue = "10", required = false) int count) {
        log.info("Самые популярные {} фильмов в базе: {}", count, filmService.getCountOfTheBestFilms(count));
        return filmService.getCountOfTheBestFilms(count);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film ) {
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(userId, id);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id, @PathVariable int userId) {
        filmService.removeLike(userId, id);
    }
}
