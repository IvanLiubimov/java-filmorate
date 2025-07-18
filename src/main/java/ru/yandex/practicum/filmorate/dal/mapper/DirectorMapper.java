package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component("directorMapper")
public class DirectorMapper implements RowMapper<Director> {

    @Override
    public Director mapRow(ResultSet rs, int rowNum) throws SQLException {
        Long directorId = rs.getLong("id");
        String name = rs.getString("name");

        return Director.builder()
                .id(directorId)
                .name(name)
                .build();
    }
}
