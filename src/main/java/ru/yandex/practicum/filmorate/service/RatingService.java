package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    public Collection<Rating> getListOfRating() {
        return ratingRepository.findAllGenres();
    }

    public Optional<Rating> getRating(Integer ratingId) {
        return ratingRepository.getRatingById(ratingId);
    }
}