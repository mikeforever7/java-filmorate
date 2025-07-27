package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
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
            log.debug("В логине содержатся пробелы {}", user.getLogin());
            throw new ValidationException("Логин не должен содержать пробелы");
        }
        if (!StringUtils.hasText(user.getName())) {
            log.info("Если не указано имя, именем становится логин {}", user.getLogin());
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        log.info("Пользователь {} добавлен", user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User newUser) {
        log.info("Пробуем обновить данные пользователя на {}", newUser);
        if (newUser.getId() == 0) {
            log.debug("id не корректен");
            throw new ValidationException("Должен быть указан корректный id");
        }
        if (exists(newUser)) {
            log.debug("id={} найден", newUser.getId());
            User oldUser = users.get(newUser.getId());
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
            log.info("Пользователь {} обновлен", oldUser);
            return oldUser;
        } else {
            log.debug("Пользователль с id={} не найден", newUser.getId());
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

    private boolean exists(User newUser) {
        return users.containsValue(newUser);
    }

}
