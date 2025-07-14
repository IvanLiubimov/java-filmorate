package ru.yandex.practicum.filmorate.dal;




import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedEventOperation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class FeedRepository {
    private final JdbcTemplate jdbcTemplate;

    public Collection<FeedEvent> findByUserId(Long userId) {
        String sql = "SELECT * FROM user_feeds WHERE user_id = ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, this::mapRowToFeedEvent, userId);
    }

    /*public void save(FeedEvent event) {
        String sql = "INSERT INTO user_feeds (user_id, event_type, operation, entity_id, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                event.getTimestamp());
    }*/

    public void save(FeedEvent event) {
        jdbcTemplate.update(
                "INSERT INTO user_feeds (user_id, event_type, operation, entity_id, timestamp) VALUES (?, ?, ?, ?, ?)",
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                Timestamp.from(event.getTimestamp())  // Конвертация Instant → SQL Timestamp
        );
    }

    /*private FeedEvent mapRowToFeedEvent(ResultSet rs, int rowNum) throws SQLException {
        return FeedEvent.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .eventType(FeedEventType.valueOf(rs.getString("event_type")))
                .operation(FeedEventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getLong("timestamp"))
                .build();
    }*/

    private FeedEvent mapRowToFeedEvent(ResultSet rs, int rowNum) throws SQLException {
        return FeedEvent.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .eventType(FeedEventType.valueOf(rs.getString("event_type")))
                .operation(FeedEventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("timestamp").toInstant())  // Конвертация SQL Timestamp → Instant
                .build();
    }
}
