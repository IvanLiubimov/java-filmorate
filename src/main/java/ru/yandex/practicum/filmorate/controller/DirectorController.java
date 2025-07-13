package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
public class DirectorController {
    private final DirectorService directorService;

    @GetMapping // GET /directors - Список всех режиссёров
    public Collection<Director> getAllDirectors() {
        log.info("Получен HTTP запрос вывод списка режиссеров");
        return directorService.getAllDirectors();
    }

    @GetMapping ("{id}")  // GET /directors/{id}- Получение режиссёра по id
    public ResponseEntity<Director> getlDirectorById(@PathVariable Long id) {
        log.info("Получен HTTP запрос на получение режиссера по id: {}", id);
        Director Director = directorService.getDirectorById(id);
        return ResponseEntity.ok(Director);
    }

    @PostMapping // POST /directors - Создание режиссёра
    public Director createDirector(@RequestBody Director director) {
        log.info("Получен HTTP запрос на создание режиссера: {}", director);
        return directorService.createDirector(director);
    }

    @PutMapping // PUT /directors - Изменение режиссёра
    public Director updateDirector(@RequestBody Director newDirector) {
        log.info("Получен HTTP запрос на обновление режиссера: {}", newDirector);
        return directorService.updateDirector(newDirector);
    }

    @DeleteMapping("/{id}") // DELETE /directors/{id} - Удаление режиссёра
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        log.info("Получен HTTP запрос на удаление режиссера по id: " + id);
        directorService.deleteDirector(id);
        return ResponseEntity.ok().build();
    }

}
