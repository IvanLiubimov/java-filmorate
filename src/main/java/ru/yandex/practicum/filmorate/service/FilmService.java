package ru.yandex.practicum.filmorate.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.FeedEventOperation;
import ru.yandex.practicum.filmorate.validator.FilmValidator;
import ru.yandex.practicum.filmorate.validator.UserValidator;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
	private final FilmRepository filmRepository;
	private final FilmValidator filmValidator;
	private final UserValidator userValidator;
	private final FeedService feedService;
	private final DirectorRepository directorRepository;

	public static final int CINEMA_BIRTH_YEAR = 1895;

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

	public Collection<Film> mostPopular(Integer count, Integer year, Integer genreId) {
		if (count != null && count < 0) {
			throw new ConditionsNotMetException("Нужное количество не может быть отрицательным");
		}
		if (genreId != null && genreId < 0) {
			throw new ConditionsNotMetException("Айди жанра не может быть отрицательным");
		}
		if (year != null && year < CINEMA_BIRTH_YEAR) {
			throw new ConditionsNotMetException("Год не может быть раньше появления кинематографа");
		}
		return filmRepository.mostPopular(count, year, genreId);
	}

	public Collection<Film> getAllFilms() {
		return filmRepository.getAllFilms();
	}

	public Film createFilm(Film film) {
		log.info("Вызван метод createFilm с фильмом: {}", film);
		List<Genre> genresList = checkFilmHasDuplicatedGenres(film);
		film.setGenres(genresList);
		filmValidator.validate(film);
		return filmRepository.createFilm(film);
	}

	public Film update(Film newFilm) {
		filmValidator.validate(newFilm);
		List<Genre> genresList = checkFilmHasDuplicatedGenres(newFilm);
		newFilm.setGenres(genresList);
		return filmRepository.updateFilm(newFilm);
	}

	public void deleteFilm(Long filmId) {
		filmRepository.deleteFilm(filmId);
	}

	public Collection<Film> getCommonFilms(Long userId, Long friendId) {
		return filmRepository.getCommonFilms(userId, friendId);
	}

	public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
		if (!directorRepository.directorExists(directorId)) {
			throw new NotFoundException("Такого режиссера у нас нет");
		}
		return filmRepository.getFilmsByDirectorSortedByLikes(directorId);
	}

	public List<Film> getFilmsByDirectorSortedByYears(long directorId) {
		directorRepository.directorExists(directorId);
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

	private List<Genre> checkFilmHasDuplicatedGenres(Film film) {
		return film.getGenres()
                .stream()
                .distinct()
				.toList();
	}
}

