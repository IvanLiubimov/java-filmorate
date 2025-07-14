package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private final FilmValidator filmValidator;

    public InMemoryFilmStorage(FilmValidator filmValidator) {
        this.filmValidator = filmValidator;
    }

    @Override
    public Collection<Film> getListOfFilms() {
        return films.values();
    }

    public Film createFilm(Film film) {
        filmValidator.validate(film);
        film.setId(generateId());
        films.put(film.getId(), film);
        log.info("Успешно обработал HTTP запрос на создание фильма: {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film newFilm) {
            log.info("Принят HTTP запрос на обновление фильма: {}", newFilm);
            if (newFilm.getId() == null) {
                throw new ConditionsNotMetException("Id должен быть указан");
            }
            if (films.containsKey(newFilm.getId())) {
                Film oldFilm = films.get(newFilm.getId());
                filmValidator.validate(newFilm);
                oldFilm.setName(newFilm.getName());
                oldFilm.setReleaseDate(newFilm.getReleaseDate());
                oldFilm.setDescription(newFilm.getDescription());
                oldFilm.setDuration(newFilm.getDuration());
                log.info("Успешно обработал HTTP запрос на обновление фильма: {}", newFilm);
                return newFilm;
            }
            log.warn("Ошибка при обновлении фильма");
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");

    }

    @Override
    public Film getFilmById(Long filmId) {
        Film film = films.get(filmId);
        if (film == null) {
            throw new NotFoundException("Фильм с id = " + filmId + " не найден");
        }
        return film;
    }

    @Override
    public Collection<Film> getSortedFilms(Integer count) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    private long generateId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
