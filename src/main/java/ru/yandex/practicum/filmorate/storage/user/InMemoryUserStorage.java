package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getAllUsers() {
        return users.values();
    }

    public User getUserById(long id) {
        return users.get(id);
    }

    public void addUser(User user) {
        users.put(user.getId(), user);
    }

    public long getNextId() {
        long currentMaxId = users.keySet()
                .stream().mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public boolean exists(User newUser) {
        return users.containsValue(newUser);
    }
}
