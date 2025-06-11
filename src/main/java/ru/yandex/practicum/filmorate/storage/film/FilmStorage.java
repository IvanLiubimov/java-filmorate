package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getListOfFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getFilmById (Long id);
}
