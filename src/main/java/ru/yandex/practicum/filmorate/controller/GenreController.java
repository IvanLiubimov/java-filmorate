package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/genres")
@RequiredArgsConstructor
public class GenreController {
   // GET /genres — возвращает список объектов содержащих жанр
   // GET /genres/{id} — возвращает объект содержащий жанр с идентификатором id

    private final GenreService genreService;

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Получен HTTP запрос вывод списка жанров");
        return genreService.getListOfGenres();
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<Genre> getGenre(@PathVariable int genreId) {
        log.info("Получен HTTP запрос на получение жанра по id: {}", genreId);
        Genre genre = genreService.getGenre(genreId);
        return ResponseEntity.ok(genre);
    }
}
