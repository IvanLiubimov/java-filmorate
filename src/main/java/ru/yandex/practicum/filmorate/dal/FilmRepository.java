package ru.yandex.practicum.filmorate.dal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dal.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {
    FilmResultSetExtractor filmResultSetExtractor = new FilmResultSetExtractor();

    private static final String FIND_ALL_FILMS = "SELECT f.*, " +
            "f.rating_id, " +
            "r.name AS rating_name, " +
            "fg.genre_id, " +
            "g.name AS genre_name, " +
            "fdir.director_id, " +
            "dir.name AS director_name, " +
            "fl.user_id AS like_user_id " +
            "FROM films f " +
            "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
            "LEFT JOIN rating r ON f.rating_id = r.id " +
            "LEFT JOIN films_directors fdir ON f.id = fdir.film_id " +
            "LEFT JOIN directors dir ON fdir.director_id = dir.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id";
    private static final String CREATE_FILM =
            "INSERT INTO films (name, description, releaseDate, duration, rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, releaseDate = ?, duration = ?, rating_id = ? WHERE id = ?";
    private static final String GET_FILM_BY_ID = "SELECT f.*, " +
            "f.rating_id, " +
            "r.name AS rating_name, " +
            "fg.genre_id, " +
            "g.name AS genre_name, " +
            "fdir.director_id, " +
            "dir.name AS director_name, " +
            "fl.user_id AS like_user_id " +
            "FROM films f " +
            "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
            "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
            "LEFT JOIN rating r ON f.rating_id = r.id " +
            "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
            "LEFT JOIN films_directors fdir ON f.id = fdir.film_id " +
            "LEFT JOIN directors dir ON fdir.director_id = dir.id " +
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


    public Collection<Film> mostPopular(Integer count, Integer year, Integer genreId) {
        StringBuilder query = new StringBuilder("""
    WITH top_films AS (
        SELECT f.id
        FROM films f
        LEFT JOIN film_likes fl ON f.id = fl.film_id
""");

        List<Object> params = new ArrayList<>();

        if (year != null) {
            query.append(" WHERE EXTRACT(YEAR FROM f.releaseDate) = ?");
            params.add(year);
        }

        if (genreId != null) {
            if (year == null) query.append(" WHERE");
            else query.append(" AND");

            query.append("""
        f.id IN (
            SELECT fg.film_id FROM films_genres fg WHERE fg.genre_id = ?
        )
    """);
            params.add(genreId);
        }

        query.append("""
        GROUP BY f.id
        ORDER BY COUNT(fl.user_id) DESC
        LIMIT ?
    )
    SELECT f.id,
           f.name,
           f.description,
           f.releaseDate,
           f.duration,
           f.rating_id,
           r.name AS rating_name,
           fg.genre_id,
           g.name AS genre_name,
           fdir.director_id,
           dir.name AS director_name,
           COALESCE(l.like_count, 0) AS like_count
    FROM films f
    JOIN top_films tf ON f.id = tf.id
    LEFT JOIN films_genres fg ON f.id = fg.film_id
    LEFT JOIN genres g ON fg.genre_id = g.genre_id
    LEFT JOIN rating r ON f.rating_id = r.id
    LEFT JOIN films_directors fdir ON f.id = fdir.film_id
    LEFT JOIN directors dir ON fdir.director_id = dir.id
    LEFT JOIN (
        SELECT film_id, COUNT(user_id) AS like_count
        FROM film_likes
        GROUP BY film_id
    ) l ON f.id = l.film_id
    ORDER BY like_count DESC, f.id ASC
""");

        params.add(count);

        return jdbcTemplate.query(query.toString(), new FilmResultSetExtractor(), params.toArray());
    }

    public Collection<Film> getAllFilms() {
        return jdbcTemplate.query(FIND_ALL_FILMS, new FilmResultSetExtractor());
    }

    public Film createFilm(Film film) {
        if (film.getRating() != null && !isRatingValid(film.getRating().getId())) {
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

        log.info("Полученные режиссёры у фильма: {}", film.getDirectors());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                if (genre != null && genre.getId() != null && isGenreExists(genre.getId())) {
                    jdbcTemplate.update("INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)", id, genre.getId());
                } else {
                    throw new NotFoundException("Invalid genre id: " + (genre != null ? genre.getId() : "null"));
                }
            }
        }
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            log.info("Создан фильм с id = {}. Проверяем режиссёров: {}", id, film.getDirectors());
            for (Director director : film.getDirectors()) {
                if (director != null && director.getId() != null && isDirectorExists(director.getId())) {
                    log.info("Пытаемся добавить связь: film_id = {}, director_id = {}", id, director.getId());
                    jdbcTemplate.update("INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)", id, director.getId());
                } else {
                    throw new NotFoundException("Invalid director id: " + (director != null ? director.getId() : "null"));
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

		jdbcTemplate.update("DELETE FROM films_genres WHERE film_id = ?", newFilm.getId());
		if (newFilm.getGenres() != null && !newFilm.getGenres().isEmpty()) {
			for (Genre genre : newFilm.getGenres()) {
				String updateGenresSql = "INSERT INTO films_genres (film_id, genre_id) VALUES (?, ?)";
					jdbcTemplate.update(updateGenresSql, newFilm.getId(), genre.getId());
			}
		}

		jdbcTemplate.update("DELETE FROM films_directors WHERE film_id = ?", newFilm.getId());
        if (newFilm.getDirectors() != null) {
            for (Director director : newFilm.getDirectors()) {
                jdbcTemplate.update("INSERT INTO films_directors (film_id, director_id) VALUES (?, ?)",
                        newFilm.getId(), director.getId());
            }
        }
        return newFilm;
    }

    public List<Film> getFilmsByDirectorSortedByLikes(long directorId) {
        String querySortByLikes = "SELECT f.*, " +
                "       f.rating_id, " +
                "       r.name AS rating_name, " +
                "       fg.genre_id, " +
                "       g.name AS genre_name, " +
                "       fdir.director_id, " +
                "       dir.name AS director_name, " +
                "       fl.user_id AS like_user_id " +
                "FROM films f " +
                "JOIN films_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN films_directors fdir ON f.id = fdir.film_id " +
                "LEFT JOIN directors dir ON fdir.director_id = dir.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY " +
                "(SELECT COUNT(*) FROM film_likes fl2 WHERE fl2.film_id = f.id) DESC," +
                "f.id ASC";

        return jdbcTemplate.query(querySortByLikes, new FilmResultSetExtractor(), directorId);
    }

    public List<Film> getFilmsByDirectorSortedByYears(long directorId) {
        String querySortByYear = "SELECT f.*, " +
                "       f.rating_id, " +
                "       r.name AS rating_name, " +
                "       fg.genre_id, " +
                "       g.name AS genre_name, " +
                "       fdir.director_id, " +
                "       dir.name AS director_name, " +
                "       fl.user_id AS like_user_id " +
                "FROM films f " +
                "JOIN films_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN films_directors fdir ON f.id = fdir.film_id " +
                "LEFT JOIN directors dir ON fdir.director_id = dir.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE fd.director_id = ? " +
                "ORDER BY f.releaseDate ASC";

        return jdbcTemplate.query(querySortByYear, new FilmResultSetExtractor(), directorId);
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

	public Collection<Film> getCommonFilms(Long userId, Long friendId) {
		String getCommonFilmsSql = ""
				+ "SELECT "
					+ "f.*, "
					+ "g.genre_id AS genre_id, "
					+ "g.name AS genre_name, "
					+ "r.name AS rating_name, "
					+ "fd.director_id, "
					+ "d.name AS director_name "
				+ "FROM films AS f "
				+ "LEFT JOIN rating AS r ON f.rating_id = r.id "
				+ "LEFT JOIN films_genres AS fg ON f.id = fg.film_id "
				+ "LEFT JOIN genres AS g ON g.genre_id = fg.genre_id "
				+ "LEFT JOIN film_likes AS fl1 ON fl1.film_id = f.id "
				+ "LEFT JOIN film_likes AS fl2 ON fl2.film_id = f.id "
				+ "LEFT JOIN films_directors AS fd ON fd.film_id = f.id "
				+ "LEFT JOIN directors AS d ON d.id = fd.director_id "
				+ "WHERE fl1.user_id = ? AND fl2.user_id = ?";
		return jdbcTemplate.query(getCommonFilmsSql, new FilmResultSetExtractor(), userId, friendId);
	}

    public boolean isDirectorExists(Long directorId) {
        String sql = "SELECT COUNT(*) FROM directors WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, directorId);
        return count != null && count > 0;
    }

    public List<Film> getFilmByDirector(String query) {
        String sql = ""
        		+ "SELECT f.*, " +
	                "f.rating_id, " +
	                "r.name AS rating_name, " +
	                "fg.genre_id, " +
	                "g.name AS genre_name, " +
	                "fdir.director_id, " +
	                "dir.name AS director_name, " +
	                "fl.user_id AS like_user_id " +
                "FROM films f " +
                "JOIN films_directors fd ON f.id = fd.film_id " +
                "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN films_directors fdir ON f.id = fdir.film_id " +
                "LEFT JOIN directors dir ON fdir.director_id = dir.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE dir.name ILIKE ? " +
                "ORDER BY f.id;";

        return jdbcTemplate.query(sql, new FilmResultSetExtractor(), "%" + query + "%");
    }

    public List<Film> getFilmByTitle(String query) {
        String sql = ""
        		+ "SELECT f.*, " +
	                "f.rating_id, " +
	                "r.name AS rating_name, " +
	                "fg.genre_id, " +
	                "g.name AS genre_name, " +
	                "fdir.director_id, " +
	                "dir.name AS director_name, " +
	                "fl.user_id AS like_user_id " +
                "FROM films f " +
                "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN films_directors fdir ON f.id = fdir.film_id " +
                "LEFT JOIN directors dir ON fdir.director_id = dir.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE f.name ILIKE ? " +
                "ORDER BY f.id DESC;";

        return jdbcTemplate.query(sql, new FilmResultSetExtractor(), "%" + query + "%");
    }

    public List<Film> searchAll(String query) {
        String sql = ""
        		+ "SELECT f.*, " +
	                "f.rating_id, " +
	                "r.name AS rating_name, " +
	                "fg.genre_id, " +
	                "g.name AS genre_name, " +
	                "fdir.director_id, " +
	                "dir.name AS director_name, " +
	                "fl.user_id AS like_user_id " +
                "FROM films f " +
                "LEFT JOIN films_genres fg ON f.id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN rating r ON f.rating_id = r.id " +
                "LEFT JOIN films_directors fdir ON f.id = fdir.film_id " +
                "LEFT JOIN directors dir ON fdir.director_id = dir.id " +
                "LEFT JOIN film_likes fl ON f.id = fl.film_id " +
                "WHERE f.name ILIKE ? OR dir.name ILIKE ? " +
				"ORDER BY f.id DESC;";

        return jdbcTemplate.query(sql, new FilmResultSetExtractor(), "%" + query + "%", "%" + query + "%");
    }

	public void deleteFilm(Long filmId) {
		delete(DELETE_FILM, filmId);
	}



}
