package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.validator.FilmValidator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class RatingRepository extends BaseRepository {

    FilmValidator filmValidator = new FilmValidator();
    public RatingRepository(JdbcTemplate jdbcTemplate,  @Qualifier("ratingMapper") RowMapper<Rating> mapper) {
        super(jdbcTemplate, mapper);
    }

    private final String FIND_ALL_RATING = "SELECT r.* FROM rating r";
    private final String FIND_RATING_BY_ID = "SELECT r.* FROM rating r WHERE id = ?";

    public Collection<Rating> findAllGenres() {
        return getAll(FIND_ALL_RATING);
    }

    public Optional<Rating> getRatingById(Integer id) {
        Optional<Rating> ratingOpt = findOne(FIND_RATING_BY_ID, id);
        if (ratingOpt.isEmpty()) {
            throw new NotFoundException("Invalid rating id: " + id);
        }
        return ratingOpt;
    }

   // public boolean isRatingValid(int ratingId) {
   //     Integer count = jdbcTemplate.queryForObject(
   //             "SELECT COUNT(*) FROM rating WHERE id = ?",
   //             Integer.class,
   //             ratingId);
//
   //     return count != null && count == 1;
   // }
}
