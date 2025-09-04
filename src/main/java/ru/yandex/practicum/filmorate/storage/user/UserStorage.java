package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    Collection<User> findAll();

    Optional<User> getUserById(long id);

    Set<User> getUserFriends(User user);

    User addUser(User user);

    User updateUser(User user);

    void addFriend(User user, long friendId);

    void deleteFriend(User user, long friendId);

    boolean existsById(long userId);
}
