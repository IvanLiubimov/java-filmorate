package ru.yandex.practicum.filmorate.dal.mapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Rating;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component ("ratingMapper")
public class RatingMapper implements RowMapper<Rating> {
    @Override
    public Rating mapRow(ResultSet rs, int rowNum) throws SQLException {
        int ratingId = rs.getInt("id");
        String name = rs.getString("name");

        return Rating.builder()
                .id(ratingId)
                .name(name)
                .build();
    }
}
