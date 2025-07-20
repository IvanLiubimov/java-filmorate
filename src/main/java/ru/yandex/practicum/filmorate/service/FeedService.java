package ru.yandex.practicum.filmorate.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dal.FeedRepository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.enums.FeedEventOperation;
import ru.yandex.practicum.filmorate.model.enums.FeedEventType;
import ru.yandex.practicum.filmorate.validator.UserValidator;

@Service
@RequiredArgsConstructor
public class FeedService {
    private final FeedRepository feedRepository;
    private final UserValidator userValidator;
    private final Logger log = LoggerFactory.getLogger(FeedService.class);



   public Collection<FeedEvent> getUserFeed(Long userId) {
       if (!userValidator.userExists(userId)) {
           throw new NotFoundException("User with id " + userId + " not found");
       }
       return feedRepository.findByUserId(userId);
   }

    public void addLikeEvent(Long userId, Long filmId, FeedEventOperation operation) {
        // 1. Проверяем существование пользователя и фильма


        // 2. Для REMOVE проверяем существование соответствующего ADD
        if (operation == FeedEventOperation.REMOVE) {
            boolean hasAdd = feedRepository.existsByUserAndEntityAndType(
                    userId, filmId, FeedEventType.LIKE, FeedEventOperation.ADD);
            if (!hasAdd) {
                return;
            }
        }

        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.LIKE)
                .operation(operation)
                .entityId(filmId)
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }

    public void addFriendEvent(Long userId, Long friendId, FeedEventOperation operation) {
        // 1. Проверяем существование обоих пользователей
        if (!userValidator.userExists(userId)) {
            throw new NotFoundException("User not found");
        }
        if (!userValidator.userExists(friendId)) {
            throw new NotFoundException("Friend not found");
        }

        // 2. Для REMOVE проверяем существование ADD
        if (operation == FeedEventOperation.REMOVE) {
            boolean hasAdd = feedRepository.existsByUserAndEntityAndType(
                    userId, friendId, FeedEventType.FRIEND, FeedEventOperation.ADD);
            if (!hasAdd) {
                return;
            }
        }

        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.FRIEND)
                .operation(operation)
                .entityId(friendId)
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }

    public void addReviewEvent(Long userId, Long reviewId, FeedEventOperation operation) {
        // 1. Проверяем существование пользователя и отзыва


        // 2. Для REMOVE проверяем существование ADD
        if (operation == FeedEventOperation.REMOVE) {
            boolean hasAdd = feedRepository.existsByUserAndEntityAndType(
                    userId, reviewId, FeedEventType.REVIEW, FeedEventOperation.ADD);
            if (!hasAdd) {
                return;
            }
        }

        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.REVIEW)
                .operation(operation)
                .entityId(reviewId)
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }

    public void addReviewLikeEvent(Long userId, Long reviewId, FeedEventOperation operation) {
        // Добавим проверку, чтобы избежать дублирования
        if (operation == FeedEventOperation.REMOVE) {
            // Проверим, было ли соответствующее ADD событие
            boolean existsAddEvent = feedRepository.existsByUserAndEntityAndType(
                    userId,
                    reviewId,
                    FeedEventType.REVIEW,
                    FeedEventOperation.ADD
            );

            if (!existsAddEvent) {

                return; // Не добавляем событие REMOVE если не было ADD
            }
        }

        FeedEvent event = FeedEvent.builder()
                .userId(userId)
                .eventType(FeedEventType.REVIEW)
                .operation(operation)
                .entityId(reviewId)
                .timestamp(System.currentTimeMillis())
                .build();
        feedRepository.save(event);
    }
}