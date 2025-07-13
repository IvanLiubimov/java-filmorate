package ru.yandex.practicum.filmorate.dal;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import java.util.Collection;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {

    public DirectorRepository(JdbcTemplate jdbcTemplate,  @Qualifier("directorMapper") RowMapper<Director> mapper) {
        super(jdbcTemplate, mapper);
    }

    private static final String FIND_ALL_DIRECTORS = "SELECT dir.* FROM directors dir";
    private static final String CREATE_DIRECTOR =
            "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_QUERY =
            "UPDATE directors SET name = ? WHERE id = ?";
    private static final String GET_DIRECTOR_BY_ID = "SELECT dir.* FROM directors dir WHERE id = ?";

    public Collection<Director> getAllDirectors() {
        return getAll(FIND_ALL_DIRECTORS);
    }

    public Optional<Director> getDirectorById(Long id) {
        return findOne(GET_DIRECTOR_BY_ID, id);
    }

    public Director createDirector(Director director) {
        long id = create(CREATE_DIRECTOR,
                director.getName());
        director.setId(id);
        return director;
    }

    public Director updateDirector(Director director) {
        update(UPDATE_QUERY,
                director.getName(),
                director.getId()
        );
        return director;
    }

    public void deleteDirector(Long id) {
        String query = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(query, id);
    }

}
