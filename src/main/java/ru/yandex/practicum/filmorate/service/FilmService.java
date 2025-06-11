package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(Long filmId, Long userId) {
        if (isFilmHasLikeFromUser(filmId, userId)) {
            throw new ConditionsNotMetException("Фильму уже ставили лайк");
        }
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);
        film.getLikes().add(user.getId());
        user.getLikedFilms().put(filmId, film);
        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        if (isFilmHasLikeFromUser(filmId, userId)) {
            Film film = filmStorage.getFilmById(filmId);
            User user = userStorage.getUserById(userId);
            if (!film.getLikes().isEmpty()) {
                film.getLikes().remove(filmId);
            }
            user.getLikedFilms().remove(filmId);
            return film;
        }
        throw new NotFoundException("Фильм не был вами пролайкан");
    }

    public Collection<Film> tenMostPopular(Integer count) {
        return filmStorage.getListOfFilms().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .collect(Collectors.toList());
    }

    boolean isFilmHasLikeFromUser(Long filmId, Long userId) {
        try {
            User user = userStorage.getUserById(userId);
            return user.getLikedFilms().containsKey(filmId); //ecли не лайкал то false
        } catch (NotFoundException e) {
            throw e;
        }
    }


}
