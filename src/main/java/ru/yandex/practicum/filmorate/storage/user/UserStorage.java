package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.http.ResponseEntity;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getListOfUsers();

    User createUser(User user);

    void deleteUser(User user);

    User updateUser(User user);

    User getUserById(Long Id);
}
