package ru.yandex.practicum.filmorate.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
   private final FilmStorage filmStorage;
   private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен HTTP запрос вывод списка фильмов");
        return filmStorage.getListOfFilms();
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Получен HTTP запрос на создание фильма: {}", film);
        return filmStorage.createFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        return filmStorage.updateFilm(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public Film addLike(@PathVariable Long id,
                                      @PathVariable Long userId) {
        return filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public Film deleteLike(@PathVariable Long id,
                                      @PathVariable Long userId) {
        return filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopular(@RequestParam(defaultValue = "10") Integer count) {
        return filmService.tenMostPopular(count);
    }



}


