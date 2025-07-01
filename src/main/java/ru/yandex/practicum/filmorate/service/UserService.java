package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final UserRepository userRepository;

    public void addFriend(Long id, long friendId) {
        userRepository.addFriend(id, friendId);
    }

    public void deleteFriend(Long id, long friendId) {
        userRepository.deleteFriend(id, friendId);
    }

    public Collection<User> showMutualFriends(Long id, long friendId) {
       return userRepository.showMutualFriends(id, friendId);
    }

    public Collection<User> getListOfUsers() {
        return userRepository.getListOfUsers();
    }

    public Optional<User> getUser(Long userId) {
        return userRepository.getUserById(userId);
    }


    public User createUser(User user) {
        return userRepository.createUser(user);
    }


    public User editUser(User newUser) {
        return userRepository.updateUser(newUser);
    }

    public Collection<User> showFriends(Long id) {
        return userRepository.showFriends(id);
    }

    boolean isFriend(User user, User friend) {
        return user.getFriends().contains(friend.getId());
    }

}

