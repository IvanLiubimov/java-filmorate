package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ReviewRepository implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            return stmt;
        }, keyHolder);

        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());
        return getById(review.getReviewId());
    }

    @Override
    public Review update(Review review) {
        String sql = "UPDATE reviews SET content = ?, is_positive = ? WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
        return getById(review.getReviewId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getById(Long id) {
        String sql = "SELECT r.*, " +
                "(SELECT COALESCE(SUM(CASE WHEN rr.is_positive THEN 1 ELSE -1 END), 0) " +
                "FROM review_ratings rr WHERE rr.review_id = r.review_id) AS useful " +
                "FROM reviews r WHERE r.review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::mapRowToReview, id);
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
        // Сначала проверяем существование записи
        String checkSql = "SELECT COUNT(*) FROM review_ratings WHERE review_id = ? AND user_id = ?";
        int count = jdbcTemplate.queryForObject(checkSql, Integer.class, reviewId, userId);

        if (count > 0) {
            // Обновляем существующую запись
            String updateSql = "UPDATE review_ratings SET is_positive = true WHERE review_id = ? AND user_id = ?";
            jdbcTemplate.update(updateSql, reviewId, userId);
        } else {
            // Вставляем новую запись
            String insertSql = "INSERT INTO review_ratings (review_id, user_id, is_positive) VALUES (?, ?, true)";
            jdbcTemplate.update(insertSql, reviewId, userId);
        }

        // Обновляем счетчик полезности
        updateUsefulness(reviewId);
    }

    @Override
    public void addDislike(Long reviewId, Long userId) {
        // Сначала пробуем обновить
        int updated = jdbcTemplate.update(
                "UPDATE review_ratings SET is_positive = false WHERE review_id = ? AND user_id = ?",
                reviewId, userId
        );

        // Если не было строк для обновления - вставляем новую
        if (updated == 0) {
            jdbcTemplate.update(
                    "INSERT INTO review_ratings (review_id, user_id, is_positive) VALUES (?, ?, false)",
                    reviewId, userId
            );
        }
        updateUsefulness(reviewId);
    }

    @Override
    public void removeLikeDislike(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_ratings WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUsefulness(reviewId);
    }

    @Override
    public boolean existsById(Long reviewId) {
        String sql = "SELECT COUNT(*) FROM reviews WHERE review_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, reviewId);
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
}
