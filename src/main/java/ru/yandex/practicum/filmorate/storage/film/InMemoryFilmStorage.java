package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private Map<Long, Film> films = new HashMap<>();
    private final LocalDate birthdayOfCinema = LocalDate.parse("1895-12-28");

    @Override
    public Collection<Film> getListOfFilms() {
        return films.values();
    }

    public Film createFilm(Film film) {
        if (filmNameValidation(film) &&
                filmDescriptionValidation(film) &&
                filmDurationValidation(film) &&
                filmReleaseDateValidation(film)) {

            film.setId(generateId());
            films.put(film.getId(), film);
            log.info("Успешно обработал HTTP запрос на создание фильма: {}", film);
            return film;
        }
        log.warn("Ошибка при создании фильма");
        throw new ConditionsNotMetException("Данные фильма не прошли валидацию");
    }

    @Override
    public Film updateFilm(Film newFilm) {
            log.info("Принят HTTP запрос на обновление фильма: {}", newFilm);
            if (newFilm.getId() == null) {
                throw new ConditionsNotMetException("Id должен быть указан");
            }
            if (films.containsKey(newFilm.getId())) {
                Film oldFilm = films.get(newFilm.getId());
                if (filmNameValidation(newFilm)) {
                    oldFilm.setName(newFilm.getName());
                }
                if (filmReleaseDateValidation(newFilm)) {
                    oldFilm.setReleaseDate(newFilm.getReleaseDate());
                }
                if (filmDescriptionValidation(newFilm)) {
                    oldFilm.setDescription(newFilm.getDescription());
                }
                if (filmDurationValidation(newFilm)) {
                    oldFilm.setDuration(newFilm.getDuration());
                }
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

    private boolean filmNameValidation(Film film) {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
        return true;
    }

    private boolean filmDurationValidation(Film film) {
        if (film.getDuration() == null || film.getDuration().isNegative()) {
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
        return true;
    }

    private boolean filmDescriptionValidation(Film film) {
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("максимальная длина описания — 200 символов");
        }
        return true;
    }

    private boolean filmReleaseDateValidation(Film film) {
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(birthdayOfCinema)) {
            throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        return true;
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
