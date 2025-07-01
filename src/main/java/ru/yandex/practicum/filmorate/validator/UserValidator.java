package ru.yandex.practicum.filmorate.validator;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

    @Component
    public class UserValidator {

        public void validate(User user) {
            if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                throw new ConditionsNotMetException("Имейл должен быть указан и содержать символ @");
            }

            if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                throw new ConditionsNotMetException("Логин не может быть пустым и содержать пробелы");
            }

            if (user.getBirthday() == null || user.getBirthday().isAfter(LocalDate.now())) {
                throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
            }

            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
        }

        private boolean emailExists(User user, Collection<User> users) {
            return users.stream().anyMatch(u -> u.getEmail().equals(user.getEmail()));
        }
    }