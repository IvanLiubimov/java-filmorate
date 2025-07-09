package ru.yandex.practicum.filmorate.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

    @Component
    @RequiredArgsConstructor
    public class UserValidator {
        protected final JdbcTemplate jdbcTemplate;

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

        public boolean userExists(Long userId) {
            try {
                String sql = "SELECT COUNT(*) FROM users WHERE id = ?";
                Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
                return count != null && count > 0;
            } catch (NullPointerException e) {
                throw new NotFoundException("Пользователь с id=" + userId + " не найден");
            }
        }
    }