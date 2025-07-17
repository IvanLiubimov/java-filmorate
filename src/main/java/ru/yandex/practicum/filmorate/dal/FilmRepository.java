package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Rating;
import lombok.extern.slf4j.Slf4j;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {
    FilmResultSetExtractor filmResultSetExtractor = new FilmResultSetExtractor();

    private static final String FIND_ALL_FILMS = "SELECT f.*, " +
            "       f.rating_id, " +
            "       fg.genre_id, " +
            "       g.name AS genre_name, " +
            "       fl.user_id AS like_user_id, " +
            "       r.name AS rating_name " +
            "FROM films f " +
            "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
            "LEFT JOIN rating r ON f.rating_id = r.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id";
    private static final String CREATE_FILM =
            "INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String GET_FILM_BY_ID = "SELECT f.*, " +
            "f.rating_id, " +
            "fg.genre_id, " +
            "g.name AS genre_name, " +
            "fl.user_id AS like_user_id, " +
            "r.name AS rating_name " +
            "FROM films f " +
            "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
            "LEFT JOIN rating r ON f.rating_id = r.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
            "WHERE f.id = ?";


    public FilmRepository(JdbcTemplate jdbcTemplate, @Qualifier("filmMapper") RowMapper<Film> mapper) {
        super(jdbcTemplate, mapper);
    }


    public Optional<Film> getFilmById(long filmId) {
        return findOneWithExtractor(GET_FILM_BY_ID, filmId);
    }

    public void addLike(long userId, long filmId) {
        String query = "MERGE INTO film_likes (user_id, film_id) KEY (user_id, film_id) VALUES (?, ?)";
        jdbcTemplate.update(query, userId, filmId);
    }

    public void deleteLike(Long filmId, Long userId) {
        String query = "DELETE FROM film_likes WHERE user_id = ? AND film_id = ?";
        jdbcTemplate.update(query, userId, filmId);
    }

    public Collection<Film> mostPopular(Integer count) {
        String query = """
                SELECT f.id,
        f.name,
        f.description,
        f.releaseDate,
        f.duration,
        f.rating_id,
        r.name AS rating_name,
        g.genre_id AS genre_id,
        g.name AS genre_name,
        COALESCE(fl.like_count, 0) AS like_count
        FROM films f
        LEFT JOIN (
            SELECT film_id, COUNT(*) AS like_count
            FROM film_likes
            GROUP BY film_id
        ) AS fl ON f.id = fl.film_id
        LEFT JOIN films_genres AS fg ON f.id = fg.film_id
        LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
        LEFT JOIN rating r ON f.rating_id = r.id
        ORDER BY like_count DESC, f.id ASC
        LIMIT ?
        """;
        return jdbcTemplate.query(query, filmResultSetExtractor, count);
    }

    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query(FIND_ALL_FILMS, new FilmResultSetExtractor());
    }

    public Film createFilm(Film film) {
        if (!isRatingValid(film.getRating().getId())) {
            throw new NotFoundException("Invalid rating id: " + film.getRating().getId());
        }
        long id = create(CREATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration().toSeconds(),
                film.getRating() != null ? film.getRating().getId() : null
        );
        film.setId(id);
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre != null && genre.getId() != null && isGenreExists(genre.getId())) {
                    jdbcTemplate.update("INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)", id, genre.getId());
                } else {
                    throw new NotFoundException("Invalid genre id: " + (genre != null ? genre.getId() : "null"));
                }
            }
        }
        return film;
    }

    public Film updateFilm(Film newFilm) {
        update(
                UPDATE_QUERY,
                newFilm.getName(),
                newFilm.getDescription(),
                newFilm.getReleaseDate(),
                newFilm.getDuration(),
                newFilm.getRating() != null ? newFilm.getRating().getId() : null,
                newFilm.getId()
        );
        return newFilm;
    }

    public boolean isGenreExists(Integer genreId) {
        String sql = "SELECT COUNT(*) FROM genres WHERE genre_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, genreId);
        return count != null && count > 0;
    }

    public boolean isRatingValid(int ratingId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM rating WHERE id = ?",
                Integer.class,
                ratingId);

        return count != null && count == 1;
    }

    public Collection<Film> mostPopular(Integer count, Integer genreId, Integer year) {
        log.info("Запрос в БД: получение {} популярных фильмов по жанру {} и году {}", count, genreId, year);
        String sql = """
            SELECT
                f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, r.name AS rating_name,
                g.genre_id, g.name AS genre_name
            FROM films f
            LEFT JOIN rating r ON f.rating_id = r.id
            LEFT JOIN film_likes l ON f.id = l.film_id
            LEFT JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            WHERE (:genreId IS NULL OR fg.genre_id = :genreId)
              AND (:year IS NULL OR EXTRACT(YEAR FROM f.releaseDate) = :year)
            GROUP BY
                f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, r.name, g.genre_id, g.name
            ORDER BY COUNT(l.user_id) DESC, f.id ASC
            LIMIT :count
            """;

        log.debug("SQL Query: {}", sql);
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("genreId", genreId);
        parameters.addValue("year", year);
        parameters.addValue("count", count);

        sql = """
            SELECT
                f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, r.name AS rating_name,
                g.genre_id, g.name AS genre_name
            FROM films f
            LEFT JOIN rating r ON f.rating_id = r.id
            LEFT JOIN film_likes l ON f.id = l.film_id
            LEFT JOIN films_genres fg ON f.id = fg.film_id
            LEFT JOIN genres g ON fg.genre_id = g.genre_id
            WHERE (fg.genre_id = ? OR ? IS NULL)
              AND (EXTRACT(YEAR FROM f.releaseDate) = ? OR ? IS NULL)
            GROUP BY f.id, f.name, f.description, f.releaseDate, f.duration, f.rating_id, r.name, g.genre_id, g.name
            ORDER BY COUNT(l.user_id) DESC, f.id ASC
            LIMIT ?
            """;

        // 1-й ? : fg.genre_id (genreId)
        // 2-й ? : ? IS NULL (genreId)
        // 3-й ? : EXTRACT(YEAR FROM f.releaseDate) (year)
        // 4-й ? : ? IS NULL (year)
        // 5-й ? : LIMIT (count)
        Collection<Film> films = jdbcTemplate.query(sql, new FilmResultSetExtractor(), genreId, genreId, year, year, count);

        log.debug("Получено из БД {} фильмов", films.size());
        return films;
    }
}
