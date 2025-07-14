package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;
import java.util.List;

public interface ReviewStorage {
    Review create(Review review);
    Review update(Review review);
    void delete(Long id);
    Review getById(Long id);
    List<Review> getByFilmId(Long filmId, int count);
    void addLike(Long reviewId, Long userId);
    void addDislike(Long reviewId, Long userId);
    void removeLikeDislike(Long reviewId, Long userId);
    boolean existsById(Long reviewId);
    boolean wasLike(Long userId, Long reviewId);
}
