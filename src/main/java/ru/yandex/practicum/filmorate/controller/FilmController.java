package ru.yandex.practicum.filmorate.controller;
import java.util.Collection;
import java.util.List;

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
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;

@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final ReviewService reviewService;

	@GetMapping
	public Collection<Film> getAllFilms() {
        log.info("Получен HTTP запрос вывод списка фильмов");
        return filmService.getAllFilms();
    }

    @GetMapping("{id}")
    public ResponseEntity<Film> getFilmById(@PathVariable Long id) {
        log.info("Получен HTTP запрос на получение фильма по id: {}", id);
        Film film = filmService.getFilmById(id);
        return ResponseEntity.ok(film);
    }

    @GetMapping("/search")
    public List<Film> searchFilms(
            @RequestParam String query,
            @RequestParam(defaultValue = "title") String by) {
        if (by.equals("director")) {
            return filmService.getFilmByDirector(query);
        } else if (by.equals("title")) {
            return filmService.getFilmByTitle(query);
        } else if (by.equals("title,director") || by.equals("director,title")) {
            return filmService.searchAll(query);
        } else {
            throw new ConditionsNotMetException("Неверные параметры поиска");
        }
    }

    @GetMapping("/director/{directorId}")
    public Collection<Film> getFilmsByDirector(
            @PathVariable long directorId,
            @RequestParam(defaultValue = "year") String sortBy) {
        if (sortBy.equals("year")) {
            return filmService.getFilmsByDirectorSortedByYears(directorId);
        }
        return filmService.getFilmsByDirectorSortedByLikes(directorId);
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.info("Тело запроса: {}", film);
        log.info("Режиссёры из тела: {}", film.getDirectors());
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
    public Collection<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer count,
                                            @RequestParam(required = false) Integer year,
                                            @RequestParam(required = false) Integer genreId) {
        return filmService.mostPopular(count, year, genreId);
    }

    @GetMapping("/{id}/reviews")
    public List<Review> getFilmReviews(
            @PathVariable Long id,
            @RequestParam(defaultValue = "10") int count) {
        return reviewService.getByFilmId(id, count);
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


