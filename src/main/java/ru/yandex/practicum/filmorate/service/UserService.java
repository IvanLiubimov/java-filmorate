package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(User user, User friend) {
        validateUsers(user, friend);
        if (isFriend(user, friend)) {
            throw new ConditionsNotMetException("Пользователь c логином " + friend.getLogin()
                    + " уже в друзьях у пользователя c логином " + user.getLogin());
        }
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    public void deleteFriend(User user, User friend) {
        validateUsers(user, friend);
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public Collection<User> showMutualFriends(User user, User friend) {
        validateUsers(user, friend);
        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(id -> userStorage.getUserById(id))
                .collect(Collectors.toList());
    }

    public Collection<User> showFriends (User user) {
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    boolean isFriend(User user, User friend) {
        return user.getFriends().contains(friend.getId());
    }

    private void validateUsers(User user, User friend) {
        if (user == null || friend == null) {
            throw new ConditionsNotMetException("Один или оба пользователя не указаны.");
        }
        if (user.getId() == null || friend.getId() == null) {
            throw new ConditionsNotMetException("ID пользователя не может быть null.");
        }
    }
}

