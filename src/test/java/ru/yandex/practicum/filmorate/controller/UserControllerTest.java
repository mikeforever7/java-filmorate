package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class UserControllerTest {

    @Autowired
    protected UserController userController;

    @Autowired
    private Validator validator;

    @BeforeEach
    public void setUp() {
        userController.getAllUsers().clear();
    }

    @Test
    public void createUser() {
        User user = User.builder().build();
        userController.createUser(user);
        assertEquals(1, user.getId());
        assertEquals("user@yandex.ru", user.getEmail());
        assertEquals("user", user.getLogin());
        assertEquals("name", user.getName());
        assertEquals(LocalDate.of(1985, 5, 5), user.getBirthday());
        assertEquals(1, userController.getAllUsers().size());
    }

    @Test
    public void emailIsBlankTest() {
        User user = User.builder().build();
        user.setEmail("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        System.out.println("Вот оно" + violations);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для почты");
    }

    @Test
    public void emailDoNotHaveAt() {
        User user = User.builder().build();
        user.setEmail("useryandex.ru");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для почты");
    }

    @Test
    public void loginIsEmptyTest() {
        User user = User.builder().build();
        user.setLogin("");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для логина");
    }

    @Test
    public void loginHaveSpaceTest() {
        User user = User.builder().build();
        user.setLogin(" ");
        assertThrows(ValidationException.class, () -> {
            userController.createUser(user);
        });
    }

    @Test
    public void nameEqualsLogin_WhenNameIsBlank() {
        User user = User.builder().build();
        user.setName("");
        userController.createUser(user);
        assertEquals("user", user.getName());
    }

    @Test
    public void birthdayNotBeFutureTest() {
        User user = User.builder().build();
        user.setBirthday(LocalDate.of(2025, 12, 12));
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для дня рождения");
    }
}
