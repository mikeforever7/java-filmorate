package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAllUsers() {
        log.info("Возвращаем коллекцию пользователей");
        return users.values();
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Добавляеем нового пользователя");
        if (user.getLogin().contains(" ")) {
            log.debug("Проверка логина на корректность");
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            log.info("Если не указано имя, именем становится логин");
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Обновлеям данные пользователя");
        if (newUser.getId() == 0) {
            log.debug("id = 0 или не корректен");
            throw new ValidationException("Должен быть указан корректный id");
        }
        if (users.containsKey(newUser.getId())) {
            log.debug("id найден");
            User oldUser = users.get(newUser.getId());
            log.debug("Обновляем email");
            oldUser.setEmail(newUser.getEmail());
            log.debug("Обновляем логин");
            oldUser.setLogin(newUser.getLogin());
            if (newUser.getName() == null || newUser.getName().isBlank()) {
                log.info("Если имя пустое, именем становится логин");
                oldUser.setName(newUser.getLogin());
            } else {
                log.debug("Обновляем имя");
                oldUser.setName(newUser.getName());
            }
            log.debug("Обновляем дату рождения");
            oldUser.setBirthday(newUser.getBirthday());
            log.info("Пользователь {} обновлен", oldUser);
            return oldUser;
        } else {
            log.debug("id не найден");
            throw new NotFoundException("Пользователя с id=" + newUser.getId() + " не найдено");
        }
    }

    private long getNextId() {
        log.info("Генеринуем новый id");
        long currentMaxId = users.keySet()
                .stream().mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }


}
