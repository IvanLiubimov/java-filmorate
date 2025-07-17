package ru.yandex.practicum.filmorate.dal;


import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class FeedRepository {
    private final JdbcTemplate jdbcTemplate;

    public Collection<FeedEvent> findByUserId(Long userId) {
        String sql = "SELECT * FROM user_feeds WHERE user_id = ? ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, this::mapRowToFeedEvent, userId);
    }

    public void save(FeedEvent event) {
        String sql = "INSERT INTO user_feeds (user_id, event_type, operation, entity_id, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                event.getUserId(),
                event.getEventType().name(),
                event.getOperation().name(),
                event.getEntityId(),
                new Timestamp(event.getTimestamp()));
    }

    private FeedEvent mapRowToFeedEvent(ResultSet rs, int rowNum) throws SQLException {
        return FeedEvent.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .eventType(FeedEventType.valueOf(rs.getString("event_type")))
                .operation(FeedEventOperation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("timestamp").getTime())
                .build();
    }

    public Collection<FeedEvent> findAll() {
        String sql = "SELECT * FROM user_feeds ORDER BY timestamp DESC";
        return jdbcTemplate.query(sql, this::mapRowToFeedEvent);
    }
}