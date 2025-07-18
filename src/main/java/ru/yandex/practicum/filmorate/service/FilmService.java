package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.FeedEventOperation;
import ru.yandex.practicum.filmorate.validator.DirectorValidator;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;
    private final FilmValidator filmValidator;
    private final UserValidator userValidator;
    private final FeedService feedService;
    private final DirectorValidator directorValidator;
    private final DirectorService directorService;


    public Film getFilmById(long filmId) {
        return filmRepository.getFilmById(filmId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + filmId + " не найден."));
    }

    public void addLike(Long filmId, Long userId) {
        getFilmById(filmId);
        userValidator.userExists(userId);
        filmRepository.addLike(userId, filmId);
        feedService.addLikeEvent(userId, filmId, FeedEventOperation.ADD);
    }

    public void deleteLike(Long filmId, Long userId) {
        if (!filmValidator.filmExists(filmId)) {
            throw new NotFoundException("Пользователь с id " + filmId + " не найден.");
        }
        if (!userValidator.userExists(userId)) {
            throw new NotFoundException("Пользователь с id=" + userId + " не найден");
        }
        filmRepository.deleteLike(filmId, userId);
        feedService.addLikeEvent(userId, filmId, FeedEventOperation.REMOVE);
    }

    public Collection<Film> mostPopular(Integer count) {
        return filmRepository.mostPopular(count);
    }



    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    public Film createFilm(Film film) {
        log.info("Вызван метод createFilm с фильмом: {}", film);
        filmValidator.validate(film);
        return filmRepository.createFilm(film);
    }

    public Film update(Film newFilm) {
        filmValidator.validate(newFilm);
        return filmRepository.updateFilm(newFilm);
    }


    public Collection<Film> mostPopular(Integer count, Integer genreId, Integer year) {
        log.info("Получение {} популярных фильмов по жанру {} и году {}", count, genreId, year);
        try {
            return filmRepository.mostPopular(count, genreId, year);
        } catch (DataAccessException e) {
            log.error("Ошибка при получении популярных фильмов по жанру {} и году {}", genreId, year, e);
            throw new RuntimeException("Ошибка при получении популярных фильмов", e);
        }
    }


    public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
        Director director = directorService.getDirectorById(directorId);
        directorValidator.validate(director);
        return filmRepository.getFilmsByDirectorSortedByLikes(directorId);
    }

    public List<Film> getFilmsByDirectorSortedByYears(long directorId) {
        Director director = directorService.getDirectorById(directorId);
        directorValidator.validate(director);
        return filmRepository.getFilmsByDirectorSortedByYears(directorId);
    }

    public List<Film> getFilmByDirector(String query) {
        return filmRepository.getFilmByDirector(query);
    }

    public List<Film> getFilmByTitle(String query) {
        return filmRepository.getFilmByTitle(query);
    }

    public List<Film> searchAll(String query) {
        return filmRepository.searchAll(query);
    }

}
