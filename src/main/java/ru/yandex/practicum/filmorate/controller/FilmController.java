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
import java.util.Collection;
import java.util.Comparator;
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
    public List<Film> getAllFilms() {
        List<Film> films  = filmStorage.getAllFilms();
        films.sort(Comparator.comparing(Film::getId));
        log.info("Количество фильмов в базе: {}",films.size());
        return films;
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        if (filmStorage.getFilmById(id).isEmpty()) {
            throw new NotFoundException("Фильм не найден в базе");
        }
        Film film = filmStorage.getFilmById(id).orElse(null);
        log.info("Фильм с id-{}: {}", id, film);
        return film;
    }

    @GetMapping("/director/{directorId}")
    public Set<Film> getSortedFilmsByDirectorId(@PathVariable int directorId,
                                                @RequestParam(defaultValue = "year", required = false) String sortBy) {
        Set<Film> filmsOfDirector = filmService.getSortedFilmsByDirectorId(directorId,sortBy);
        log.info("Фильмы {} режиссера с id: {}", filmsOfDirector, directorId);
        return filmsOfDirector;
    }

    @GetMapping("/popular")
    public Set<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                      @RequestParam(required = false) Integer genreId,
                                      @RequestParam(required = false) Integer year) {
        Set<Film> bestFilms;
        if (genreId == null && year == null) {
            bestFilms = filmService.getCountOfTheBestFilms(count);
            log.info("Самые популярные {} фильмов в базе: {}", count, bestFilms);
            return bestFilms;
        }
        bestFilms = filmService.getPopularFilmsByGenreOrYear(genreId, year, count);
        log.info("Самые популярные {} фильмов в жанре id = {} за год {}: {}", count, genreId, year, bestFilms);
        return bestFilms;
    }

    @GetMapping("/common")
    public Collection<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        Collection<Film> commonFilms = filmService.getCommonFilms(userId,friendId);
        log.info("Общие фильмы фильмы пользователей {} и {} в базе: {}", userId, friendId ,commonFilms);
        return commonFilms;
    }

    @GetMapping("/search")
    public Collection<Film> searchFilms(@RequestParam String query, @RequestParam String by) {
        Collection<Film> searchResult = filmService.searchFilms(query, by);
        log.info("Список фильмов по запросу by = {} в базе: {}", query, searchResult);
        return searchResult;
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film ) {
        return filmStorage.createFilm(film);
    }

    @DeleteMapping("/{id}")
    public void deleteFilmById(@PathVariable int id) {
        filmStorage.deleteFilmById(id);
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
