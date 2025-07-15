package ru.yandex.practicum.filmorate.controller;
import java.util.Collection;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

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

	@DeleteMapping("{id}")
	public ResponseEntity<Void> deleteFilm(@PathVariable Long id) {
		log.info("Получен HTTP запрос на удаление фильма по id: {}", id);
		filmService.deleteFilm(id);
		log.info("Пользователь успешно удален, id: {}", id);
		return ResponseEntity.ok().build();
	}

	@GetMapping("/common")
	public Collection<Film> getCommonFilms(@RequestParam Long userId, @RequestParam Long friendId) {
		return filmService.getCommonFilms(userId, friendId);
	}
}


