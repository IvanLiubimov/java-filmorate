package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository {

    private final String FIND_ALL_GENRES = "SELECT g.* FROM genres g";
    private final String FIND_GENRE_BY_ID = "SELECT g.* FROM genres g WHERE genre_id = ?";

    public GenreRepository(JdbcTemplate jdbcTemplate,  @Qualifier("genreMapper") RowMapper<Genre> mapper) {
        super(jdbcTemplate, mapper);
    }


    public Collection<Genre> findAllGenres() {
        return getAll(FIND_ALL_GENRES);
    }

    public Optional<Genre> getGenreById(int id) {
        Optional<Genre> genreOpt = findOne(FIND_GENRE_BY_ID, id);
        if (genreOpt.isEmpty()) {
            throw new NotFoundException("Invalid genre id: " + id);
        }
        return genreOpt;
    }
}
