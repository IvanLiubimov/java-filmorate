package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RatingService {
    private final RatingRepository ratingRepository;

    public Collection<Rating> getListOfRating() {
        return ratingRepository.findAllRating();
    }

    public Rating getRating(Integer ratingId) {
        Optional<Rating> ratingOpt = ratingRepository.getRatingById(ratingId);
        if (ratingOpt.isEmpty()) {
            throw new NotFoundException("Invalid rating id: " + ratingId);
        }
        return ratingOpt.get();
    }
}