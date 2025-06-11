package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserStorage userStorage;
    private final UserService userService;

    @GetMapping
    public Collection<User> getListOfUsers() {
       return userStorage.getListOfUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long userId) {
        log.info("Получен HTTP запрос на получение пользователя по id: {}", userId);
        User user = userStorage.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.info("Получен HTTP запрос на создание пользователя: {}", user);
        return userStorage.createUser(user);
    }

    @PutMapping
    public User editUser(@RequestBody User newUser) {
        log.info("Принят HTTP запрос на обновление пользователя: {}", newUser);
        return userStorage.updateUser(newUser);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        userService.addFriend(user, friend);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity<Void> deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        userService.deleteFriend(user, friend);
        return ResponseEntity.ok().build();
    }

    @GetMapping ("/{id}/friends")
    public Collection<User> showFriends (@PathVariable Long id) {
        User user = userStorage.getUserById(id);
        return userService.showFriends(user);
    }

    @GetMapping ("/{id}/friends/common/{otherId}")
    public Collection<User> showMutualFriends (@PathVariable Long id, @PathVariable Long otherId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(otherId);
        return userService.showMutualFriends(user, friend);
    }

}

