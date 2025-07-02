package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.validator.UserValidator;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserValidator userValidator;
    protected final JdbcTemplate jdbcTemplate;


    public void addFriend(Long id, long friendId) {
        if (id.equals(friendId)) {
            throw new ConditionsNotMetException("Нельзя добавить самого себя в друзья.");
        }

        if (!userValidator.userExists(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден.");
        }

        if (!userValidator.userExists(friendId)) {
            throw new NotFoundException("Пользователь с id " + friendId + " не найден.");
        }
        userRepository.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, long friendId) {
        if (id.equals(friendId)) {
            throw new ConditionsNotMetException("Нельзя удалить самого себя из друзей.");
        }
        if (!userValidator.userExists(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (!userValidator.userExists(friendId)) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
        userRepository.deleteFriend(id, friendId);
    }

    public Collection<User> showMutualFriends(Long id, long friendId) {
        if (id.equals(friendId)) {
            throw new ConditionsNotMetException("Нельзя посмотреть общих  друзей у самого себя.");
        }
        if (!userValidator.userExists(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        if (!userValidator.userExists(friendId)) {
            throw new NotFoundException("Пользователь с id=" + friendId + " не найден");
        }
       return userRepository.showMutualFriends(id, friendId);
    }

    public Collection<User> getListOfUsers() {
        return userRepository.getListOfUsers();
    }

    public User getUser(Long userId) {
        return userRepository.getUserById(userId)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + userId + " не найден."));
    }


    public User createUser(User user) {
        userValidator.validate(user);
        if (isEmailExists(user.getEmail())) {
            throw new ConditionsNotMetException("Такой имейл уже есть у одного из пользователей");
        }
        return userRepository.createUser(user);
    }


    public User editUser(User newUser) {
        userValidator.validate(newUser);
        return userRepository.updateUser(newUser);
    }

    public Collection<User> showFriends(Long id) {
        if (!userValidator.userExists(id)) {
            throw new NotFoundException("Пользователь с id=" + id + " не найден");
        }
        return userRepository.showFriends(id);
    }

    boolean isFriend(User user, User friend) {
        return user.getFriends().contains(friend.getId());
    }

    private boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE LOWER(email) = LOWER(?)";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }



}

