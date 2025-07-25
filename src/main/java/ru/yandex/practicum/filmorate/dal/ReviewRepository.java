package ru.yandex.practicum.filmorate.dal;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

@Repository
@RequiredArgsConstructor
public class ReviewRepository implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setInt(5, review.getUseful() != null ? review.getUseful() : 0);
            return stmt;
        }, keyHolder);

        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        review.setReviewId(generatedId);

        return review;
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());


        return getById(review.getReviewId()).orElseThrow();
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Optional<Review> getById(Long id) {
        String sql = "SELECT r.*, " +
                "(SELECT COALESCE(SUM(CASE WHEN rr.is_positive THEN 1 ELSE -1 END), 0) " +
                "FROM review_ratings rr WHERE rr.review_id = r.review_id) AS useful " +
                "FROM reviews r WHERE r.review_id = ?";
        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, this::mapRowToReview, id));
        } catch (Exception e) {
            return Optional.empty();
        }
    }


    @Override
    public List<Review> getByFilmId(Long filmId, int count) {
        String sql = "SELECT r.*, " +
                "(SELECT COALESCE(SUM(CASE WHEN rr.is_positive THEN 1 ELSE -1 END), 0) " +
                "FROM review_ratings rr WHERE rr.review_id = r.review_id) AS useful " +
                "FROM reviews r WHERE r.film_id = ? OR ? IS NULL " +
                "ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, filmId, count);
    }

    @Override
    public void addLike(Long reviewId, Long userId) {

        String sql = "MERGE INTO review_ratings (review_id, user_id, is_positive) " +
                "KEY (review_id, user_id) VALUES (?, ?, true)";
        jdbcTemplate.update(sql, reviewId, userId);

        updateUsefulness(reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {

        String sql = "MERGE INTO review_ratings (review_id, user_id, is_positive) " +
                "KEY (review_id, user_id) VALUES (?, ?, false)";
        jdbcTemplate.update(sql, reviewId, userId);

        updateUsefulness(reviewId);
    }

    @Override
    public void removeLike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ? AND is_positive = true";
        jdbcTemplate.update(sql, reviewId, userId);

        updateUsefulness(reviewId);
    }

    @Override
    public void removeDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ? AND is_positive = false";
        jdbcTemplate.update(sql, reviewId, userId);

        updateUsefulness(reviewId);
    }

    @Override
    public boolean existsById(Long reviewId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
        return count != null && count > 0;
    }

    @Override
    public boolean hasUserLike(Long reviewId, Long userId) {
        String sql = "SELECT COUNT(*) FROM review_ratings WHERE review_id = ? AND user_id = ? AND is_positive = true";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    @Override
    public boolean hasUserDislike(Long reviewId, Long userId) {
        String sql = "SELECT COUNT(*) FROM review_ratings WHERE review_id = ? AND user_id = ? AND is_positive = false";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId, userId);
        return count != null && count > 0;
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getInt("useful"))
                .build();
    }

    private void updateUsefulness(Long reviewId) {
        String sql = "UPDATE reviews SET useful = (" +
                "SELECT COALESCE(SUM(CASE WHEN is_positive THEN 1 ELSE -1 END), 0) " +
                "FROM review_ratings WHERE review_id = ?" +
                ") WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId, reviewId);
    }

    @Override
    public void removeAllRatingsForReview(Long reviewId) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ?";
        jdbcTemplate.update(sql, reviewId);
    }
}