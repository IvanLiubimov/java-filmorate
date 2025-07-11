package ru.yandex.practicum.filmorate.controller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
   private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAllFilms() {
        log.info("Получен HTTP запрос вывод списка фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping ("{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        log.info("Получен HTTP запрос на получение фильма по id: {}", id);
        Film film = filmService.getFilmById(id);
        return ResponseEntity.ok(film);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Получен HTTP запрос на создание фильма: {}", film);
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен HTTP запрос на обновление фильма: {}", newFilm);
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Long id,
                                      @PathVariable Long userId) {
        log.info("Получен HTTP запрос на добавление лайка фильму пользователем: {} {}", id, userId);
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable Long id,
                                      @PathVariable Long userId) {
        log.info("Получен HTTP запрос на удаление лайка фильма пользователем: {} {}", id, userId);
        filmService.deleteLike(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> getMostPopular(@RequestParam(defaultValue = "10") Integer count) {
        log.info("Получен HTTP запрос на вывод списка популярных фильмов");
        return filmService.mostPopular(count);
    }
}


