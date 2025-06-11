package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(Long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        if (isFriend(user, friend)) {
            throw new ConditionsNotMetException("Пользователь c логином " + friend.getLogin()
                    + " уже в друзьях у пользователя c логином " + user.getLogin());
        }
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
    }

    public void deleteFriend(Long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());
    }

    public Collection<User> showMutualFriends(Long id, long friendId) {
        User user = userStorage.getUserById(id);
        User friend = userStorage.getUserById(friendId);
        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(userId -> userStorage.getUserById(userId))
                .collect(Collectors.toList());
    }

    public Collection<User> getListOfUsers() {
        return userStorage.getListOfUsers();
    }

    public User getUser(Long userId) {
        return userStorage.getUserById(userId);
    }


    public User createUser(User user) {
        return userStorage.createUser(user);
    }


    public User editUser(User newUser) {
        return userStorage.updateUser(newUser);
    }

    public Collection<User> showFriends(Long id) {
        User user = userStorage.getUserById(id);
        return user.getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    boolean isFriend(User user, User friend) {
        return user.getFriends().contains(friend.getId());
    }


}

