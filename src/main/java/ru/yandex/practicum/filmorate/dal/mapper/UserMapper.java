package ru.yandex.practicum.filmorate.dal.mapper;


import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Component ("userMapper")
public class UserMapper implements RowMapper<User> {

    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long userId = rs.getLong("id");
        String name = rs.getString("name");
        String login = rs.getString("login");
        String email = rs.getString("email");
        LocalDate birthday = rs.getTimestamp("birthday").toLocalDateTime().toLocalDate();

        return User.builder()
                .id(userId)
                .login(login)
                .email(email)
                .name(name)
                .birthday(birthday)
                .build();
    }
}
