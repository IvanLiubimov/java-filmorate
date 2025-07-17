package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.util.Collection;
import java.util.Optional;


@Repository
public class UserRepository extends BaseRepository<User> {
    private static final String FIND_ALL_USERS = "SELECT * FROM users";
    private static final String CREATE_USER = "INSERT INTO users(name, login, email, birthday) " + "VALUES (?, ?, ?, ?)";
    private static final String FIND_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String UPDATE_USER = "UPDATE users SET  email = ?, login = ?, name = ?, birthday = ?  WHERE id = ?";

    public UserRepository(JdbcTemplate jdbc, @Qualifier("userMapper") RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    public Collection<User> getListOfUsers() {
        return getAll(FIND_ALL_USERS);
    }

    public User createUser(User user) {
        long id = create(CREATE_USER,
                user.getName(),
                user.getEmail(),
                user.getLogin(),
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
        String query = "SELECT u.* FROM friendship f " +
                "INNER JOIN users u ON f.friend_id = u.id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(query, mapper, id);
    }

    public void addFriend(Long id, Long friendId) {
        String queryCheck = "SELECT count(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(queryCheck, Integer.class, id, friendId);
        if (count != null && count > 0) {
            throw new ConditionsNotMetException("Такие друзья уже существуют");
        }

        String query1 = "INSERT INTO friendship (user_id, friend_id) VALUES (?, ?) ";
        jdbcTemplate.update(query1, id, friendId);
    }

    public void deleteFriend(Long id, long friendId) {
        String query = "DELETE FROM friendship WHERE user_id = ? AND friend_id = ?";

        jdbcTemplate.update(query, id, friendId);
    }

    public Collection<User> showMutualFriends(Long id, long friendId) {
        String query = "SELECT u.* " +
                "FROM users u " +
                "JOIN friendship f1 ON u.id = f1.friend_id " +
                "JOIN friendship f2 ON u.id = f2.friend_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(query, mapper, id, friendId);
    }


}

