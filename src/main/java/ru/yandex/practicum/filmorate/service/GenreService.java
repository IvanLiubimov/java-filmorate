package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;

    public Collection<Genre> getListOfGenres() {
        return genreRepository.findAllGenres();
    }

    public Genre getGenre(int genreId) {
        Optional<Genre> genreOpt = genreRepository.getGenreById(genreId);
        if (genreOpt.isEmpty()) {
            throw new NotFoundException("Invalid genre id: " + genreId);
        }
        return genreOpt.get();
    }
}
