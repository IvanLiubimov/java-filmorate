package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.DirectorRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public Collection<Director> getAllDirectors() {
        return directorRepository.getAllDirectors();
    }

    public Director getDirectorById(long directorId) {
        return directorRepository.getDirectorById(directorId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + directorId + " не найден."));
    }

    public Director createDirector(Director director) {
        return directorRepository.createDirector(director);
    }

    public Director updateDirector(Director newDirector) {
		if (!directorRepository.directorExists(newDirector.getId())) {
			throw new NotFoundException("Режиссер с id=" + newDirector.getId() + " не найден");
		}
        return directorRepository.updateDirector(newDirector);
    }

    public void deleteDirector(Long id) {
        if (!directorRepository.directorExists(id)) {
            throw new NotFoundException("Режиссер с id=" + id + " не найден");
        }
        directorRepository.deleteDirector(id);
    }
}
