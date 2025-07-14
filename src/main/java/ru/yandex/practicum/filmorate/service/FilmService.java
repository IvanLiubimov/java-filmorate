package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final FilmValidator filmValidator;
    private final UserValidator userValidator;


    public Film getFilmById(long filmId) {
        return filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден."));
    }

    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userValidator.userExists(userId);
        filmRepository.addLike(userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!filmValidator.filmExists(filmId)) {
            throw new NotFoundException("Пользователь с id " + filmId + " не найден.");
        }
        if (!userValidator.userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        filmRepository.deleteLike(filmId, userId);
    }

    public Collection<Film> mostPopular(Integer count) {
        return filmRepository.mostPopular(count);
    }



    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    public Film createFilm(Film film) {
        filmValidator.validate(film);
        return filmRepository.createFilm(film);
    }

    public Film update(Film newFilm) {

        filmValidator.validate(newFilm);
        return filmRepository.updateFilm(newFilm);
    }

	public void deleteFilm(Long filmId) {
		filmRepository.deleteFilm(filmId);
	}


}
