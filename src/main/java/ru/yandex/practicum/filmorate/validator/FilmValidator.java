package ru.yandex.practicum.filmorate.validator;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
    public class FilmValidator {

    JdbcTemplate jdbcTemplate = new JdbcTemplate();

        private static final LocalDate EARLIEST_RELEASE_DATE = LocalDate.of(1895, 12, 28);

        public void validate(Film film) {
            if (film.getName() == null || film.getName().isBlank()) {
                throw new ConditionsNotMetException("Название фильма не может быть пустым");
            }
            if (film.getDescription() == null || film.getDescription().length() > 200 || film.getDescription().isBlank()) {
                throw new ConditionsNotMetException("Описание не может быть пустым или длиннее 200 символов");
            }
            if (film.getDuration().isNegative() || film.getDuration().isZero()) {
                throw new ConditionsNotMetException("Продолжительность фильма должна быть положительной");
            }
            if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(EARLIEST_RELEASE_DATE)) {
                throw new ConditionsNotMetException("Дата релиза не может быть раньше 28 декабря 1895 года");
            }
        }

    }
