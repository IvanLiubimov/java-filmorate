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
        Review createdReview = reviewStorage.create(review);
        // Добавляем событие создания отзыва в ленту
        feedService.addReviewEvent(createdReview.getUserId(), createdReview.getReviewId(), FeedEventOperation.ADD);
        return createdReview;
    }

    public Review update(Review review) {
        validateReviewExists(review.getReviewId());
        validateUserAndFilm(review.getUserId(), review.getFilmId());
        Review updatedReview = reviewStorage.update(review);
        // Добавляем событие обновления отзыва в ленту
        feedService.addReviewEvent(updatedReview.getUserId(), updatedReview.getReviewId(), FeedEventOperation.UPDATE);
        return updatedReview;
    }

    public void delete(Long id) {
        Review review = getById(id); // Получаем отзыв перед удалением
        reviewStorage.delete(id);
        // Добавляем событие удаления отзыва в ленту
        feedService.addReviewEvent(review.getUserId(), review.getReviewId(), FeedEventOperation.REMOVE);
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
        // Добавляем событие лайка отзыва в ленту
        feedService.addLikeToReviewEvent(userId, reviewId, FeedEventOperation.ADD);
    }

    public void addDislike(Long reviewId, Long userId) {
        validateReviewExists(reviewId);
        userService.getUser(userId);
        reviewStorage.addDislike(reviewId, userId);
        // Добавляем событие дизлайка отзыва в ленту
        feedService.addLikeToReviewEvent(userId, reviewId, FeedEventOperation.REMOVE);
    }

    public void removeLikeDislike(Long reviewId, Long userId) {
        validateReviewExists(reviewId);
        userService.getUser(userId);
        // Перед удалением узнаем, был ли это лайк или дизлайк
        boolean wasLike = reviewStorage.wasLike(userId, reviewId);
        reviewStorage.removeLikeDislike(reviewId, userId);
        // Добавляем соответствующее событие в ленту
        if (wasLike) {
            feedService.addLikeEvent(userId, reviewId, FeedEventOperation.REMOVE);
        } else {
            feedService.addLikeEvent(userId, reviewId, FeedEventOperation.ADD);
        }
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

