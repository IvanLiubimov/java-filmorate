package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.validator.DirectorValidator;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;
    private final DirectorValidator directorValidator;

    public Collection<Director> getAllDirectors() {
        return directorRepository.getAllDirectors();
    }

    public Director getDirectorById(long directorId) {
        return directorRepository.getDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + directorId + " не найден."));
    }

    public Director createDirector(Director director) {
        directorValidator.validate(director);
        return directorRepository.createDirector(director);
    }

    public Director updateDirector(Director newDirector) {
        directorValidator.validate(newDirector);
        return directorRepository.updateDirector(newDirector);
    }

    public void deleteDirector(Long id) {
        if (!directorValidator.directorExists(id)) {
            throw new NotFoundException("Режиссер с id=" + id + " не найден");
        }
        directorRepository.deleteDirector(id);
    }
}
