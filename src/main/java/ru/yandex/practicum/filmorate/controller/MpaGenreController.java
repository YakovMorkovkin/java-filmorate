package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.FilmService;

import java.util.Set;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping()
public class MpaGenreController {
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
}