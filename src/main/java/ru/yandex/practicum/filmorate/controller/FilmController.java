package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
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

    @GetMapping
    public Collection<Film> getAllFilms(){
        return films.values();
    }

    @PostMapping
    public Film  createFilm (@RequestBody Film film) {
        log.info("Получен HTTP запрос на создание фильма: {}", film);
        film.setId(generateId());
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new ConditionsNotMetException("максимальная длина описания — 200 символов");
        }
        LocalDate birthdayOfCinema = LocalDate.parse("1895-12-28");
        if (film.getReleaseDate().isBefore(birthdayOfCinema)) {
            throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
        } if (film.getDuration().isNegative()) {
             throw new ConditionsNotMetException("Продолжительность фильма должна быть положительным числом");
        }
        films.put(film.getId(), film);
        log.info("Успешно обработал HTTP запрос на создание фильма: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Принят HTTP запрос на обновление фильма: {}", newFilm);
        if (newFilm.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                log.trace(newFilm.getName() + " пустое");
                String savedName = oldFilm.getName();
                newFilm.setName(savedName);
            }
            if (newFilm.getDescription().length() > 200) {
                log.trace(newFilm.getDescription() + " содержит больше 200 знаков");
                String savedDescription = oldFilm.getDescription();
                newFilm.setDescription(savedDescription);
            }
            LocalDate birthdayOfCinema = LocalDate.parse("1895-12-28");
            if (newFilm.getReleaseDate().isBefore(birthdayOfCinema)) {
                log.trace(newFilm.getReleaseDate() + " раньше ДР всего кино");
                LocalDate savedDate = oldFilm.getReleaseDate();
                newFilm.setReleaseDate(savedDate);
                throw new ConditionsNotMetException("Дата релиза — не раньше 28 декабря 1895 года");
            }

            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            oldFilm.setDuration(newFilm.getDuration());

            return oldFilm;
        }
        log.info("Успешно обработал HTTP запрос на обновление фильма: {}", newFilm);
        throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
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


