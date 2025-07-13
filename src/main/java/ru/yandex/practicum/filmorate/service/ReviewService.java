package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final UserService userService;
    private final FilmService filmService;

    public Review create(Review review) {
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        validateReviewExists(review.getReviewId());
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        return reviewStorage.update(review);
    }

    public void delete(Long id) {
        validateReviewExists(id);
        reviewStorage.delete(id);
    }

    public Review getById(Long id) {
        validateReviewExists(id);
        return reviewStorage.getById(id);
    }

    public List<Review> getByFilmId(Long filmId, int count) {
        if (filmId != null) {
            filmService.getFilmById(filmId);
        }
        return reviewStorage.getByFilmId(filmId, count);
    }

    public void addLike(Long reviewId, Long userId) {
        validateReviewExists(reviewId);
        userService.getUser(userId);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateReviewExists(reviewId);
        userService.getUser(userId);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeLikeDislike(Long reviewId, Long userId) {
        validateReviewExists(reviewId);
        userService.getUser(userId);
        reviewStorage.removeLikeDislike(reviewId, userId);
    }

    private void validateReviewExists(Long reviewId) {
        if (!reviewStorage.existsById(reviewId)) {
            throw new NotFoundException("Review with id " + reviewId + " not found");
        }
    }

    private void validateUserAndFilm(Long userId, Long filmId) {
        userService.getUser(userId);
        filmService.getFilmById(filmId);
    }
}

