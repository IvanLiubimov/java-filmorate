package ru.yandex.practicum.filmorate.dal;

import java.sql.Date;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import ru.yandex.practicum.filmorate.dal.mapper.FilmResultSetExtractor;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;


@Repository
public class UserRepository extends BaseRepository<User> {
	private static final String FIND_ALL_USERS = ""
    		+ "SELECT * "
    		+ "FROM users";

	private static final String CREATE_USER = ""
    		+ "INSERT INTO users(name, login, email, birthday) "
    		+ "VALUES (?, ?, ?, ?)";

	private static final String FIND_USER_BY_ID = ""
    		+ "SELECT * "
    		+ "FROM users "
    		+ "WHERE id = ?";

	private static final String UPDATE_USER = ""
    		+ "UPDATE users "
			+ "SET email = ?, login = ?, name = ?, birthday = ? "
    		+ "WHERE id = ?";

	private static final String DELETE_USER = ""
    		+ "DELETE "
    		+ "FROM users "
    		+ "WHERE id = ?";

	public UserRepository(JdbcTemplate jdbc, @Qualifier("userMapper") RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> getListOfUsers() {
        return getAll(FIND_ALL_USERS);
    }

    public User createUser(User user) {
        long id = create(CREATE_USER,
                user.getName(),
				user.getLogin(),
                user.getEmail(),
                Date.valueOf(user.getBirthday()));
        user.setId(id);
        return user;
    }

    public User updateUser(User user) {
        update(UPDATE_USER,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                Date.valueOf(user.getBirthday()),
                user.getId()
        );
        return user;
    }

    public Optional<User> getUserById(Long id) {
        return findOne(FIND_USER_BY_ID, id);
    }

    public Collection<User> showFriends(Long id) {
        String query = ""
        		+ "SELECT u.* FROM friendship f "
        		+ "INNER JOIN users u ON f.friend_id = u.id "
        		+ "WHERE f.user_id = ?";
        return jdbcTemplate.query(query, mapper, id);
    }

    public void addFriend(Long id, Long friendId) {
        String queryCheck = ""
        		+ "SELECT count(*) "
        		+ "FROM friendship "
        		+ "WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(queryCheck, Integer.class, id, friendId);

		if (count != null && count > 0) {
            throw new ConditionsNotMetException("Такие друзья уже существуют");
        }

        String query1 = ""
        		+ "INSERT INTO friendship (user_id, friend_id) "
        		+ "VALUES (?, ?) ";
        jdbcTemplate.update(query1, id, friendId);
    }

    public void deleteFriend(Long id, long friendId) {

		String query = ""
    			+ "DELETE "
    			+ "FROM friendship "
    			+ "WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(query, id, friendId);
    }

    public Collection<User> showMutualFriends(Long id, long friendId) {
		String query = "" + "SELECT u.* "
				+
                "FROM users u " +
                "JOIN friendship f1 ON u.id = f1.friend_id " +
                "JOIN friendship f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(query, mapper, id, friendId);
    }

	public void deleteUser(Long userId) {
		delete(DELETE_USER, userId);
	}

	public boolean isUserExists(Long userId) {
		String isUserExistsSql = ""
				+ "SELECT EXISTS (SELECT 1 "
								+ "FROM users "
								+ "WHERE id = ?";
		return jdbcTemplate.queryForObject(isUserExistsSql, Boolean.class);
	}

    public Collection<Film> getRecommendedFilms(long userId) {
        // 1. Находим пользователей с максимальным пересечением по лайкам
        String similarUsersQuery = "SELECT fl2.user_id AS similar_user_id, " +
                "COUNT(*) AS common_likes_count " +
                "FROM film_likes fl1 " +
                "JOIN film_likes fl2 ON fl1.film_id = fl2.film_id AND fl1.user_id != fl2.user_id " +
                "WHERE fl1.user_id = ? " +
                "GROUP BY fl2.user_id " +
                "ORDER BY common_likes_count DESC " +
                "LIMIT 1";

        // 2. Если нет пользователей с общими лайками, возвращаем null (обработаем в сервисе)
        List<Long> similarUserIds = jdbcTemplate.query(
                similarUsersQuery,
                (rs, rowNum) -> rs.getLong("similar_user_id"),
                userId
        );

        if (similarUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        long similarUserId = similarUserIds.get(0);

        // 3. Находим фильмы, которые понравились похожему пользователю, но не текущему
        String recommendedFilmsQuery = "SELECT f.*, " +
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
                "WHERE f.id IN (" +
                "SELECT film_id " +
                "FROM film_likes " +
                "WHERE user_id = ? AND film_id NOT IN (" +
                "SELECT film_id " +
                "FROM film_likes " +
                "WHERE user_id = ?" +
                ")" +
                ")";

        return jdbcTemplate.query(recommendedFilmsQuery, new FilmResultSetExtractor(), similarUserId, userId);
    }
}

