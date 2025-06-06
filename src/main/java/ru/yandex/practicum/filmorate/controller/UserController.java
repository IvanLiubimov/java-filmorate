package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ErrorResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getListOfUsers() {
        log.info("Получен HTTP запрос вывод списка пользователей");
        return users.values();
    }

    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        log.info("Получен HTTP запрос на создание пользователя: {}", user);
        try {
            if (userEmailValidation(user) && userLoginValidation(user) && userBirthdayValidation(user) && !isEmailExists(user)) {
                long userId = generateId();
                user.setId(userId);
                if (userNameValidation(user)) {
                    user.setName(user.getLogin());
                }
                users.put(userId, user);
                log.info("Успешно обработал HTTP запрос на создание пользователя: {}", user);
                return ResponseEntity.ok(user);
            }
            return ResponseEntity.badRequest().build();
        } catch (ConditionsNotMetException e) {
            log.warn("Ошибка при добавлении пользователя: {}", e.getMessage());
            String error = e.getMessage();
            return ResponseEntity.badRequest().body(new ErrorResponse(error));
        }
    }

    @PutMapping
    public ResponseEntity<?> editUser(@RequestBody User newUser) {

        log.info("Принят HTTP запрос на обновление пользователя: {}", newUser);

        try {
            if (newUser.getId() == null) {
                long newUserId = users.values().stream()
                        .filter(user1 -> user1.getEmail().equals(newUser.getEmail()))
                        .map(User::getId)
                        .findFirst()
                        .orElse(0L);
                newUser.setId(newUserId);
            }
            if (users.containsKey(newUser.getId())) {
                log.trace("Пользователь найден");
                User oldUser = users.get(newUser.getId());

                if (userEmailValidation(newUser)) {
                    oldUser.setEmail(newUser.getEmail());
                }
                if (userLoginValidation(newUser)) {
                    oldUser.setLogin(newUser.getLogin());
                }
                if (!userNameValidation(newUser)) {
                    oldUser.setName(newUser.getName());
                }
                if (userBirthdayValidation(newUser)) {
                    oldUser.setBirthday(newUser.getBirthday());
                }

                log.info("Успешно обработал HTTP запрос на обновление пользователя: {}", newUser);
                return ResponseEntity.ok(oldUser);
            }
            throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
        } catch (ConditionsNotMetException | NotFoundException e) {
            log.warn("Ошибка при обновлении пользователя: {}", e.getMessage());
            String error = e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(error));
        }
    }

    private boolean isEmailExists(User user) {
        if (users.values().stream()
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
            throw new ConditionsNotMetException("Такой имеил уже есть у одного из пользователей");
        }
        return false;
    }

    private boolean userNameValidation(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            return true;
        }
        return false;
    }

    private boolean userEmailValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ @");
        }
        return true;
    }

    private boolean userLoginValidation(User user) {
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("логин не может быть пустым и содержать пробелы");
        }
        return true;
    }

    private boolean userBirthdayValidation(User user) {
        if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("дата рождения не может быть в будущем");
        }
        return true;
    }

    private long generateId() {
        long maxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        maxId++;
        return maxId;
    }
}
