package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.model.enums.FeedEventOperation;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserValidator userValidator;

    public Collection<FeedEvent> getUserFeed(Long userId) {
        userValidator.userExists(userId);
        return feedRepository.findByUserId(userId);
    }

    public void addLikeEvent(Long userId, Long filmId, FeedEventOperation operation) {
        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.LIKE)
                .operation(operation)
                .entityId(filmId)
                //.timestamp(Instant.now())
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }

    public void addFriendEvent(Long userId, Long friendId, FeedEventOperation operation) {
        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.FRIEND)
                .operation(operation)
                .entityId(friendId)
                //.timestamp(Instant.now())
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }

    public void addReviewEvent(Long userId, Long reviewId, FeedEventOperation operation) {
        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.REVIEW)
                .operation(operation)
                .entityId(reviewId)
                //.timestamp(Instant.now())
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }

    public void addLikeToReviewEvent(Long userId, Long reviewId, FeedEventOperation operation) {
        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.LIKE)
                .operation(operation)
                .entityId(reviewId)  // Явно указываем, что это ID отзыва
                //.timestamp(Instant.now())
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }
}
