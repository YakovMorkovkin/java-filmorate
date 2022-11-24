package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final HashMap<Integer, Film> films = new HashMap<>();
    private int filmId;

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("Количество фильмов в базе: {}",films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createUser(@Valid @RequestBody Film film) {
        boolean isExist = false;
        for (Film f : films.values()) {
            if (f.getName().equals(film.getName())) {
                isExist = true;
                break;
            }
        }
        if (isExist) {
            throw new ValidationException("Фильм с названием " + film.getName() + " уже существует.");
        } else if (film.getReleaseDate().isBefore(Film.DATE)) {
            throw new ValidationException("До " + Film.DATE + " фильмов не снимали.");
        } else {
            film.setId(filmIdGenerator());
            films.put(film.getId(),film);
        }
        log.debug("Добавлен фильм: {}", film);
        return films.get(film.getId());
    }

    @PutMapping
    public Film updateUser(@Valid @RequestBody Film film) {
        boolean isExist = false;
        for (Film f : films.values()) {
            if (f.getId() == film.getId()) {
                isExist = true;
                break;
            }
        }
        if (film.getReleaseDate().isBefore(Film.DATE)) {
            throw new ValidationException("До " + Film.DATE + " фильмов не снимали.");
        } else if (isExist) {
            films.remove(film.getId());
            films.put(film.getId(),film);
        } else throw new ValidationException("Фильма с id: " + film.getId()+ " не существует");
        log.debug("Обновлён фильм: {}", film);
        return films.get(film.getId());
    }

    int filmIdGenerator(){
        return ++filmId;
    }
}
