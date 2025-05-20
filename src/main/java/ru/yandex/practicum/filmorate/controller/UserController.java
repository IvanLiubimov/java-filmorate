package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private Map<Long, User> listOfUsers = new HashMap<>();

    @GetMapping
    public Collection<User> getListOfUsers(){
        return listOfUsers.values();
    }

    @PostMapping
    public User createUser (@RequestBody User user){
        log.info("Получен HTTP запрос на создание пользователя: {}", user);
        long userId = generateId();
        user.setId(userId);
        if (user.getName() == null) {
            user.setName(user.getLogin());
        }
        if (user.getEmail().isEmpty() || !user.getEmail().contains("@")){
            throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ @");
        }
        boolean exists = listOfUsers.values().stream()
                .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()));
        if (exists){
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")){
            throw new ConditionsNotMetException("логин не может быть пустым и содержать пробелы");
        }

        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException ("дата рождения не может быть в будущем");
        }
        listOfUsers.put(userId, user);
        log.info("Успешно обработал HTTP запрос на создание пользователя: {}", user);
        return user;
    }

    @PutMapping
    public User editUser(@RequestBody User newUser) {
        log.info("Принят HTTP запрос на обновление пользователя: {}", newUser);

        if (newUser.getId() == null){
            if (newUser.getEmail() != null) {
                long newUserId = listOfUsers.values().stream()
                        .filter(user1 -> user1.getEmail().equals(newUser.getEmail()))
                        .map(User::getId)
                        .findFirst()
                        .orElse(0L);
                newUser.setId(newUserId);
                throw new ConditionsNotMetException( "Укажите либо Id либо имейл");
            }
        }
        if (listOfUsers.containsKey(newUser.getId())) {
            log.trace("Пользлватель найден");
            User oldUser = listOfUsers.get(newUser.getId());
         //  boolean exists = listOfUsers.values().stream()
         //          .anyMatch(user1 -> user1.getEmail().equals(newUser.getEmail()));
         //  if (exists) {
         //      log.trace("Найден одинаковый имейл");
         //      throw new DuplicatedDataException("Этот имейл уже используется");
         //  }
            if (newUser.getEmail() == null) {
                log.trace("Пустой имейл");
                String savedEmail = oldUser.getEmail();
                newUser.setEmail(savedEmail);
            }
            if (newUser.getLogin() == null) {
                log.trace("Пустой логин");
                String savedLogin = oldUser.getLogin();
                newUser.setLogin(savedLogin);
            }
            if (newUser.getName() == null) {
                log.trace("Пустое имя");
                String savedName = oldUser.getName();
                newUser.setName(savedName);
            }

            oldUser.setEmail(newUser.getEmail());
            oldUser.setLogin(newUser.getLogin());
            oldUser.setName(newUser.getName());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Успешно обработал HTTP запрос на обновление пользователя: {}", newUser);
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");

    }



    private long generateId() {
        long maxId = listOfUsers.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        maxId++;
        return maxId;
    }
}
