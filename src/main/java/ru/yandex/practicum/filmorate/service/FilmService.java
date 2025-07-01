package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmRepository filmRepository;

    public Optional<Film> getFilmById(long filmId) {
        return filmRepository.getFilmById(filmId);
    }

    public void addLike(Long filmId, Long userId) {
        filmRepository.addLike(userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        filmRepository.deleteLike(filmId, userId);
    }

    public Collection<Film> mostPopular(Integer count) {
        return filmRepository.MostPopular(count);
    }



    public Collection<Film> getAllFilms() {
        return filmRepository.getAllFilms();
    }

    public Film createFilm(Film film) {
        return filmRepository.createFilm(film);
    }

    public Film update(Film newFilm) {
        return filmRepository.updateFilm(newFilm);
    }

   // boolean isFilmHasLikeFromUser(Long filmId, User user) {
   //     try {
   //         return user.getLikedFilms().containsKey(filmId); //ecли не лайкал то false
   //     } catch (NotFoundException e) {
   //         throw e;
   //     }
   // }

}
