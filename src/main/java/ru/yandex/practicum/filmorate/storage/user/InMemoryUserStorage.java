package ru.yandex.practicum.filmorate.storage.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Logger log = LoggerFactory.getLogger(InMemoryUserStorage.class);
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> getUserById(long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Set<User> getUserFriends(User user) {
        return user.getFriends().stream()
                .map(this::getUserById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Override
    public User addUser(User user) {
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User newUser) {
        User oldUser = users.get(newUser.getId());
        if (newUser.getEmail() != null) {
            log.debug("Обновляем email на {}", newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
        }
        if (newUser.getLogin() != null) {
            log.debug("Обновляем логин на {}", newUser.getLogin());
            oldUser.setLogin(newUser.getLogin());
        }
        if (newUser.getName() != null && !newUser.getName().trim().isEmpty()) {
            log.debug("Обновляем имя на {}", newUser.getName());
            oldUser.setName(newUser.getName());
        }
        if (newUser.getBirthday() != null) {
            log.debug("Обновляем дату рождения на {}", newUser.getBirthday());
            oldUser.setBirthday(newUser.getBirthday());
        }
        log.info("Пользователь с id={} обновлен", oldUser.getId());

        users.put(oldUser.getId(), oldUser);
        return oldUser;
    }

    public void addFriend(User user, long friendId) {
        user.getFriends().add(friendId);
    }

    public void deleteFriend(User user, long friendId) {
        user.getFriends().remove(friendId);
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream().mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @Override
    public boolean existsById(long userId) {
        return users.containsKey(userId);
    }
}
