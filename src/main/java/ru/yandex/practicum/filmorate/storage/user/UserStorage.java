package ru.yandex.practicum.filmorate.storage.user;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    Collection<User> getListOfUsers();

    User createUser(User user);

    User updateUser(User user);

    User getUserById(Long id);

    boolean isEmailExists(String email);
}
