package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    InMemoryFilmStorage inMemoryFilmStorage;

    @Autowired
    public FilmService(InMemoryFilmStorage inMemoryFilmStorage) {
        this.inMemoryFilmStorage = inMemoryFilmStorage;
    }

    public void addLike(Integer userId, Integer filmId) {
        inMemoryFilmStorage.getFilmById(filmId).getLikes().add(Long.valueOf(userId));
        log.debug("Фильму с id-{}, пользователь с id-{} поставил like", filmId, userId);
    }

    public void disLike(Integer userId, Integer filmId) {
        if (inMemoryFilmStorage.getFilmById(filmId).getLikes().contains(Long.valueOf(userId))) {
            inMemoryFilmStorage.getFilmById(filmId).getLikes().remove(Long.valueOf(userId));
            log.debug("Фильму с id-{}, пользователь с id-{} поставил dislike", filmId, userId);
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    public Set<Film> getCountOfTheBestFilms(Integer count) {
        return inMemoryFilmStorage.getAllFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toSet());
    }

    private int compare(Film f0, Film f1) {
        int result = f0.getLikes().size() - f1.getLikes().size();
        result = -1 * result;
        return result;
    }
}
