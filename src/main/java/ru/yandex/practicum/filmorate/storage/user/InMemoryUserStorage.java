package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private final UserValidator userValidator;

    public Collection<User> getListOfUsers() {
        return users.values();
    }

    public InMemoryUserStorage(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @Override
    public User createUser(User user) {
        userValidator.validate(user);
        if (isEmailExists(user.getEmail())) {
            throw new ConditionsNotMetException("Такой имейл уже есть у одного из пользователей");
        }
        long userId = generateId();
        user.setId(userId);
        users.put(userId, user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {

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

                userValidator.validate(newUser);
                oldUser.setEmail(newUser.getEmail());
                oldUser.setLogin(newUser.getLogin());
                oldUser.setName(newUser.getName());
                oldUser.setBirthday(newUser.getBirthday());

                log.info("Успешно обработал HTTP запрос на обновление пользователя: {}", newUser);
                return newUser;

            }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    @Override
    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с id = " + id + " не найден");
        }
        return user;
    }

    private long generateId() {
        long maxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        maxId++;
        return maxId;
    }

    @Override
    public boolean isEmailExists(String email) {
        return users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }
}





//private boolean isEmailExists(User user) {
//    if (users.values().stream()
//            .anyMatch(user1 -> user1.getEmail().equals(user.getEmail()))) {
//        throw new ConditionsNotMetException("Такой имеил уже есть у одного из пользователей");
//    }
//    return false;
//}
//
//private boolean userNameValidation(User user) {
//    if (user.getName() == null || user.getName().isEmpty()) {
//        return true;
//    }
//    return false;
//}
//
//private boolean userEmailValidation(User user) {
//    if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
//        throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ @");
//    }
//    return true;
//}
//
//private boolean userLoginValidation(User user) {
//    if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
//        throw new ConditionsNotMetException("логин не может быть пустым и содержать пробелы");
//    }
//    return true;
//}
//
//private boolean userBirthdayValidation(User user) {
//    if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
//        throw new ConditionsNotMetException("дата рождения не может быть в будущем");
//    }
//    return true;
//}
//