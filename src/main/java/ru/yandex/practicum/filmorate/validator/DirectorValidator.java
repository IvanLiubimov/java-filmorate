package ru.yandex.practicum.filmorate.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Director;

@Component
public class DirectorValidator {
    @Autowired
    JdbcTemplate jdbcTemplate;

    public void validate(Director director) {
        if (director.getName() == null || director.getName().isBlank()) {
            throw new ConditionsNotMetException("Название фильма не может быть пустым");
        }
    }

    public boolean directorExists(Long directorId) {
        String sql = "SELECT COUNT(*) FROM directors WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, directorId);
        return count != null && count > 0;
    }
}
