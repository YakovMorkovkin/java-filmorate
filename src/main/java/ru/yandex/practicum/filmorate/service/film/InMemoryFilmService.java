package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final InMemoryFilmStorage inMemoryFilmStorage;
    private final InMemoryUserStorage inMemoryUserStorage;
    @Override
    public void addLike(Integer userId, Integer filmId) {
        if (inMemoryUserStorage.getUsers().containsKey(userId)) {
            getFilm(filmId).addLike(userId);
            log.info("Фильму с id-{}, пользователь с id-{} поставил like", filmId, userId);
        } else throw new NotFoundException("Пользователь с id-" + userId + " не найден в базе");
    }
    @Override
    public void removeLike(Integer userId, Integer filmId) {
        if (inMemoryUserStorage.getUsers().containsKey(userId)) {
            getFilm(filmId).removeLike(userId);
            log.info("У фильма с id-{}, пользователь с id-{} удалил like", filmId, userId);
        } else throw new NotFoundException("Пользователь с id-" + userId + " не найден в базе");
    }
    @Override
    public Set<Film> getCountOfTheBestFilms(Integer count) {
        return inMemoryFilmStorage.getAllFilms().stream()
                .sorted(this::compare)
                .limit(count)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Genre> getAllGenres() {
        return null;
    }

    @Override
    public Optional<Genre> getGenreById(int id) {
        return Optional.empty();
    }

    @Override
    public Set<Mpa> getAllMpa() {
        return null;
    }

    @Override
    public Optional<Mpa> getMpaById(int id) {
        return Optional.empty();
    }

    private int compare(Film f0, Film f1) {
        int result = f0.getLikes().size() - f1.getLikes().size();
        result = -1 * result;
        return result;
    }

    private Film getFilm(Integer filmId) {
        return inMemoryFilmStorage.getFilmById(filmId).orElse(null);
    }
}
