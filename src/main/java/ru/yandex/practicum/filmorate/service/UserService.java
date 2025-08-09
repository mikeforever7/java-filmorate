package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        log.info("Возвращаем коллекцию пользователей");
        return userStorage.getAllUsers();
    }

    public User getUserById(long id) {
        User user = userStorage.getUserById(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    public User createUser(User user) {
        log.info("Добавляеем нового пользователя");
        if (user.getLogin().contains(" ")) {
            log.warn("В логине содержатся пробелы {}", user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (!StringUtils.hasText(user.getName())) {
            log.info("Если не указано имя, именем становится логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        user.setFriends(new HashSet<>());
        userStorage.addUser(user);
        log.info("Пользователь c id={} добавлен", user.getId());
        return user;
    }

    public User updateUser(User newUser) {
        log.info("Пробуем обновить данные пользователя на {}", newUser);
        if (newUser.getId() == 0) {
            log.warn("id не корректен");
            throw new ValidationException("Должен быть указан корректный id");
        }
        if (exists(newUser)) {
            log.debug("id={} найден", newUser.getId());
            User oldUser = userStorage.getUserById(newUser.getId());
            log.debug("Обновляем email на {}", newUser.getEmail());
            oldUser.setEmail(newUser.getEmail());
            log.debug("Обновляем логин на {}", newUser.getLogin());
            oldUser.setLogin(newUser.getLogin());
            if (!StringUtils.hasText(newUser.getName())) {
                log.info("Если имя пустое, именем становится логин {}", newUser.getLogin());
                oldUser.setName(newUser.getLogin());
            } else {
                log.debug("Обновляем имя на {}", newUser.getName());
                oldUser.setName(newUser.getName());
            }
            log.debug("Обновляем дату рождения на {}", newUser.getBirthday());
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь с id={} обновлен", oldUser.getId());
            return oldUser;
        } else {
            log.warn("Пользователль с id={} не найден", newUser.getId());
            throw new NotFoundException("Пользователя с id=" + newUser.getId() + " не найдено");
        }
    }

    public Set<User> getUserFriends(long userId) {
        User user = getUserById(userId);
        return user.getFriends().stream()
                .map(this::getUserById)
                .collect(Collectors.toSet());
    }

    public Set<User> getCommonFriends(long user1, long user2) {
        Set<User> commonFriends = new HashSet<>(getUserFriends(user1));
        commonFriends.retainAll(getUserFriends(user2));
        return commonFriends;
    }

    public void addFriend(long userId, long friendId) {
        User user = getUserById(userId);
        user.getFriends().add(friendId);
        User otherUser = getUserById(friendId);
        otherUser.getFriends().add(userId);
        log.info("Юзеры id={} и id={} добавлены друг другу в друзья", userId, friendId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.debug("Удаляем у юзера с Id={} друга с Id={}", userId, friendId);
        User user = getUserById(userId);
        user.getFriends().remove(friendId);
        log.debug("Также удаляем у юзера с Id={} друга с Id={}", friendId, userId);
        User otherUser = getUserById(friendId);
        otherUser.getFriends().remove(userId);
        log.info("Юзеры id={} и id={} удалены друг у друга из друзей", userId, friendId);
    }

    public long getNextId() {
        log.info("Генеринуем новый id");
        return userStorage.getNextId();
    }

    private boolean exists(User newUser) {
        return userStorage.exists(newUser);
    }
}
