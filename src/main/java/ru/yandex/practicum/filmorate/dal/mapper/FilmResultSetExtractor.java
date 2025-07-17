package ru.yandex.practicum.filmorate.dal.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;

@Slf4j
public class FilmResultSetExtractor implements ResultSetExtractor<List<Film>> {

    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException {
        Map<Long, Film> idToFilm = new LinkedHashMap<>();

        while (rs.next()) {
            long currentId = rs.getLong("id");
            Film film = idToFilm.computeIfAbsent(currentId, id -> {
                try {
                    String name = rs.getString("name");
                    String description = rs.getString("description");
                    LocalDate releaseDate = rs.getDate("releaseDate").toLocalDate();
                    long durationInSec = rs.getLong("duration");
                    Duration duration = Duration.ofSeconds(durationInSec);

                    Integer ratingId = rs.getObject("rating_id", Integer.class);
                    String ratingName = rs.getString("rating_name");

                    Rating rating = null;
                    if (ratingId != null && ratingName != null) {
                        rating = new Rating(ratingId, ratingName);
                    }

                    return Film.builder()
                            .id(id)
                            .name(name)
                            .description(description)
                            .releaseDate(releaseDate)
                            .duration(duration)
                            .rating(rating)
                            .genres(new ArrayList<>())
                            .directors(new ArrayList<>())
                            .build();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            });

            int genreId = rs.getInt("genre_id");
            String genreName = rs.getString("genre_name");

            if (genreId > 0 && genreName != null) {
                boolean genreExists = film.getGenres().stream()
                        .anyMatch(g -> g.getId() == genreId);
                if (!genreExists) {
                    film.getGenres().add(new Genre(genreId, genreName));
                }
            }

            Long directorId = rs.getObject("director_id", Long.class);
            String directorName = rs.getString("director_name");

            if (directorId != null && directorName != null) {
                boolean directorExists = film.getDirectors().stream()
                        .anyMatch(d -> d.getId().equals(directorId));

                if (!directorExists) {
                    film.getDirectors().add(new Director(directorId, directorName));
                }
            }
        }
        return new ArrayList<>(idToFilm.values());
    }
}
