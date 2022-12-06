package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final HashMap<Integer, Film> films = new HashMap<>();
    private Integer filmId = 0;

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilmById(int id) {
        return films.get(id);
    }

    @Override
    public Film createFilm(Film film) {
        if (isExistByName(film)) {
            throw new ValidationException("Фильм с названием " + film.getName() + " уже существует.");
        } else {
            film.setId(filmIdGenerator());
            films.put(film.getId(), film);
            log.debug("Добавлен фильм: {}", film);
        }
        return films.get(film.getId());
    }

    @Override
    public Film updateFilm(Film film) {
        if (isExistById(film)) {
            films.remove(film.getId());
            films.put(film.getId(), film);
            log.debug("Обновлён фильм: {}", film);
        } else throw new ValidationException("Фильма с id: " + film.getId() + " не существует");
        return films.get(film.getId());
    }

    private boolean isExistByName(Film film) {
        boolean isExist = false;
        for (Film f : films.values()) {
            if (f.getName().equals(film.getName())) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    private boolean isExistById(Film film) {
        boolean isExist = false;
        for (Film f : films.values()) {
            if (f.getId() == film.getId()) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    private int filmIdGenerator() {
        return ++filmId;
    }
}
