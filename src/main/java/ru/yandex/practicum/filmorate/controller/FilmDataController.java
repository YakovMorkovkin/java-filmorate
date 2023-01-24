package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import javax.validation.Valid;
import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
public class FilmDataController {
    private final FilmService filmService;

    @GetMapping("/genres")
    public Set<Genre> getAllGenres() {
        log.info("Список жанров");
        return filmService.getAllGenres();
    }

    @GetMapping("/genres/{id}")
    public Genre getGenreById(@PathVariable int id) {
        log.info("Жанр с id - {}", id);
        return filmService.getGenreById(id).orElse(null);
    }

    @GetMapping("/mpa")
    public Set<Mpa> getAllMpa() {
        log.info("Список жанров");
        return filmService.getAllMpa();
    }

    @GetMapping("/mpa/{id}")
    public Mpa getMpaById(@PathVariable int id) {
        log.info("Жанр с id - {}", id);
        return filmService.getMpaById(id).orElse(null);
    }

    @GetMapping("/directors")
    public Set<Director> getAllDirectors() {
        log.info("Список режиссеров");
        return filmService.getAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public Director getDirectorById(@PathVariable int id) {
        log.info("Режиссер с id - {}", id);
        return filmService.getDirectorById(id).orElse(null);
    }

    @PostMapping("/directors")
    public Director createDirector(@Valid  @RequestBody Director director ) {
        return filmService.createDirector(director);
    }

    @PutMapping("/directors")
    public Director updateDirector(@Valid @RequestBody Director director) {
        return filmService.updateDirector(director);
    }

    @DeleteMapping("/directors/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void removeLike(@PathVariable int id) {
        filmService.removeDirector(id);
    }
}