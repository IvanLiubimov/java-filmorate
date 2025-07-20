package ru.yandex.practicum.filmorate.dal.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import ru.yandex.practicum.filmorate.model.Film;

@Component("filmMapper")
public class FilmMapper implements RowMapper<Film> {

	@Override
	public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
		Film film = new Film();
		film.setId(rs.getLong("id"));
		film.setName(rs.getString("name"));
		film.setDescription(rs.getString("description"));
		film.setReleaseDate(rs.getTimestamp("releaseDate").toLocalDateTime().toLocalDate());

		long duration = rs.getLong("duration");
		film.setDuration(Duration.ofSeconds(duration));

		return film;
	}
}
