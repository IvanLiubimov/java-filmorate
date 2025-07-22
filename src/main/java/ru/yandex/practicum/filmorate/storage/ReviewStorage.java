package ru.yandex.practicum.filmorate.storage;

import java.util.List;
import java.util.Optional;

import ru.yandex.practicum.filmorate.model.Review;

public interface ReviewStorage {
	Review create(Review review);

	Review update(Review review);

	void delete(Long id);

	Optional<Review> getById(Long id);

	List<Review> getByFilmId(Long filmId, int count);

	void addLike(Long reviewId, Long userId);

	void addDislike(Long reviewId, Long userId);

	void removeLike(Long reviewId, Long userId);

	void removeDislike(Long reviewId, Long userId);

	boolean existsById(Long reviewId);

	boolean hasUserLike(Long reviewId, Long userId);

	boolean hasUserDislike(Long reviewId, Long userId);

	void removeAllRatingsForReview(Long reviewId);
}