package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Repository
public class RatingRepository extends BaseRepository<Rating> {

    public RatingRepository(JdbcTemplate jdbcTemplate,  @Qualifier("ratingMapper") RowMapper<Rating> mapper) {
        super(jdbcTemplate, mapper);
    }

    private static final String FIND_ALL_RATING = "SELECT r.* FROM rating r";
    private static final String FIND_RATING_BY_ID = "SELECT r.* FROM rating r WHERE id = ?";

    public Collection<Rating> findAllRating() {
        return getAll(FIND_ALL_RATING);
    }

    public Optional<Rating> getRatingById(Integer id) {
        Optional<Rating> ratingOpt = findOne(FIND_RATING_BY_ID, id);
        return ratingOpt;
    }
}
