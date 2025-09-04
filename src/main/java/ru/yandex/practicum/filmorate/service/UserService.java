package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserStorage userStorage;
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getAllUsers() {
        log.info("Возвращаем коллекцию пользователей");
        return userStorage.findAll();
    }

    public User getUserById(long id) {
        Optional<User> user = userStorage.getUserById(id);
        if (user.isEmpty()) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user.get();
    }

    public User createUser(User user) {
        log.info("Добавляеем нового пользователя");
        log.info("День {}", user.getBirthday());
        validateLogin(user.getLogin());
        log.info("Прошла проверка на пробелы в логине");
        if (!StringUtils.hasText(user.getName())) {
            log.info("Если не указано имя, именем становится логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        User savedUser = userStorage.addUser(user);
        log.info("Пользователь c id={} добавлен", user.getId());
        return savedUser;
    }

    public User updateUser(User newUser) {
        log.info("Пробуем обновить данные пользователя");
        if (newUser.getId() < 1) {
            log.warn("id не корректен");
            throw new ValidationException("Должен быть указан корректный id");
        }
        if (!userStorage.existsById(newUser.getId())) {
            log.debug("id={} не найден", newUser.getId());
            throw new NotFoundException("Пользователя с id=" + newUser.getId() + " не найдено");
        }
        log.debug("Пользователь с id={} найден", newUser.getId());
        if (newUser.getLogin() != null) {
            validateLogin(newUser.getLogin());
        }
        if (newUser.getEmail() != null) {
            validateEmail(newUser.getEmail());
        }
        if (newUser.getBirthday() != null) {
            validateBirthday(newUser.getBirthday());
        }

        return userStorage.updateUser(newUser);
    }

    public Set<User> getUserFriends(long userId) {
        User user = getUserById(userId);
        return userStorage.getUserFriends(user);
    }

    public Set<User> getCommonFriends(long user1, long user2) {
        Set<User> commonFriends = new HashSet<>(getUserFriends(user1));
        commonFriends.retainAll(getUserFriends(user2));
        return commonFriends;
    }

    public void addFriend(long userId, long friendId) {
        log.info("Пользователь {} добавляет в друзья пользователя {}", userId, friendId);
        User user = getUserById(userId);
        getUserById(friendId);
        userStorage.addFriend(user,friendId);
        log.info("Юзер id={} добавлен юзеру id={} в друзья", friendId, userId);
    }

    public void deleteFriend(long userId, long friendId) {
        log.debug("Удаляем у юзера с id={} друга с id={}", userId, friendId);
        User user = getUserById(userId);
        getUserById(friendId);
        userStorage.deleteFriend(user, friendId);
        log.info("Юзер id={} удален у юзера id={} из друзей", friendId, userId);
    }

    public void validateLogin(String login) {
        if (login.contains(" ")) {
            log.warn("В логине содержатся пробелы {}", login);
            throw new ValidationException("Логин не должен содержать пробелы");
        }
    }

    public void validateEmail(String email) {
        if (!(email.contains("@"))
                || !(email.contains("."))
                || email.trim().isEmpty()) {
            throw new ValidationException("Некорректный Email");
        }
    }

    public void validateBirthday(LocalDate birthday) {
        if (birthday.isAfter(LocalDate.now())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
