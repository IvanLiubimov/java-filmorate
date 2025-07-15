package ru.yandex.practicum.filmorate.dal;

import java.util.Collection;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.dal.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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

    private static final String DELETE_FILM = ""
    		+ "DELETE "
    		+ "FROM films "
    		+ "WHERE id = ?";

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

	public void deleteFilm(Long filmId) {
		delete(DELETE_FILM, filmId);
	}

	public Collection<Film> getCommonFilms(Long userId, Long friendId) {
		String getCommonFilmsSql = ""
				+ "SELECT f.*,"
						+ "g.genre_id AS genre_id, "
						+ "g.name AS genre_name, "
						+ "r.name AS rating_name "
				+ "FROM films AS f "
				+ "JOIN rating AS r ON f.rating_id = r.id "
				+ "JOIN films_genres AS fg ON f.id = fg.film_id "
				+ "JOIN genres AS g ON g.genre_id = fg.genre_id "
				+ "JOIN film_likes AS fl1 ON fl1.film_id = f.id "
				+ "JOIN film_likes AS fl2 ON fl2.film_id = f.id "
				+ "WHERE fl1.user_id = ? AND fl2.user_id = ?";
		return jdbcTemplate.query(getCommonFilmsSql, new FilmResultSetExtractor(), userId, friendId);
	}
}
