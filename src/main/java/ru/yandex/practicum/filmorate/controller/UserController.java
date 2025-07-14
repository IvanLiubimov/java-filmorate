package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.FeedEvent;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FeedService;
import ru.yandex.practicum.filmorate.service.UserService;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final FeedService feedService;

    @GetMapping
    public Collection<User> getListOfUsers() {
        log.info("Получен HTTP запрос на получение всех пользователей");
       return userService.getListOfUsers();

    }

    @GetMapping("/{userId}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        log.info("Получен HTTP запрос на получение пользователя по id: {}", userId);
        User user = userService.getUser(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен HTTP запрос на создание пользователя: {}", user);
        return userService.createUser(user);
    }

    @PutMapping
    public User editUser(@RequestBody User newUser) {
        log.info("Принят HTTP запрос на обновление пользователя: {}", newUser);
        return userService.editUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен HTTP запрос на добавление в друзья пользователем " + id + " пользователя " + friendId);
        userService.addFriend(id, friendId);
        log.info("HTTP запрос на добавление в друзья пользователем " + id + " пользователя " + friendId + "успешен");
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Получен HTTP запрос на удаление из друзей пользователем " + id + " пользователя " + friendId);
        userService.deleteFriend(id, friendId);
        log.info("HTTP запрос на удаление из друзей пользователем " + id + " пользователя " + friendId + " успешен");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/friends")
    public Collection<User> showFriends(@PathVariable Long id) {
        log.info("Получен HTTP запрос на показ друзей пользователем " + id);
        return userService.showFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> showMutualFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Получен HTTP запрос на показ общих из друзей пользователем " + id + "  и пользователя " + otherId);
        return userService.showMutualFriends(id, otherId);
    }

    @GetMapping("/feed")
    public Collection<FeedEvent> getAllFeed() {
        log.info("Получен запрос на получение всей ленты событий");
        return feedService.getAllFeed();
    }

    @GetMapping("/{userid}/feed")
    public Collection<FeedEvent> getUserFeed(@PathVariable Long userid) {
        log.info("Получен запрос на ленту пользователя с ID: {}", userid);
        return feedService.getUserFeed(userid);
    }








}

