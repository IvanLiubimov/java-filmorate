package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private Map<Long, Film> films = new HashMap<>();
    private final LocalDate birthdayOfCinema = LocalDate.parse("1895-12-28");

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен HTTP запрос вывод списка фильмов");
        return films.values();
    }

    @PostMapping
    public ResponseEntity<?> createFilm(@RequestBody Film film) {
        try {
            log.info("Получен HTTP запрос на создание фильма: {}", film);
            if (filmNameValidation(film) && filmDescriptionValidation(film) && filmDurationValidation(film) &&
                    filmReleaseDateValidation(film)) {
                film.setId(generateId());
                films.put(film.getId(), film);
            }
            log.info("Успешно обработал HTTP запрос на создание фильма: {}", film);
            return ResponseEntity.ok(film);
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка при добавлении фильма: {}", e.getMessage());
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping
    public ResponseEntity<?> update(@RequestBody Film newFilm) {
        try {
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
                return ResponseEntity.ok(oldFilm);
            }
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        } catch (ConditionsNotMetException | NotFoundException e) {
            log.warn("Ошибка при обновлении фильма: {}", e.getMessage());
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(error);
        }
    }

    private boolean filmNameValidation (Film film) {
        if (film.getName() == null || film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
        return true;
    }

    private boolean filmDurationValidation (Film film) {
        if (film.getDuration() == null || film.getDuration().isNegative()) {
            throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
        return true;
    }

    private boolean filmDescriptionValidation (Film film) {
        if (film.getDescription() == null || film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("максимальная длина описания — 200 символов");
        }
        return true;
    }

    private boolean filmReleaseDateValidation (Film film) {
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


