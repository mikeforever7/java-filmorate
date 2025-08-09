package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Set;


@SpringBootTest
public class FilmControllerTest {

    @Autowired
    protected FilmController filmController;

    @Autowired
    private Validator validator;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createFilm() {
        Film film = Film.builder().build();
        filmController.createFilm(film);
        assertEquals(1, film.getId());
        assertEquals("Название", film.getName());
        assertEquals("Описание", film.getDescription());
        assertEquals(LocalDate.of(1985, 12, 12), film.getReleaseDate());
        assertEquals(10, film.getDuration());
        assertEquals(1, filmController.getAllFilms().size());
    }

    @Test
    public void nameIsBlankTest() {
        Film film = Film.builder().build();
        film.setName("");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для имени");
    }

    @Test
    public void descriptionIsTooLong() {
        Film film = Film.builder().build();
        film.setDescription("eoiugnewiounbweipnbeoфывафыrthrtrtjhrehvkjnwcdokmwrombomkbomkbomdmpvd" +
                "ерцацупцукпуцпупупpeopvniewugripunbeirgsdhrthkomergojkmrrhrethrthrethrthrthrthrthrbbdfgehgerthrehre" +
                "ethomrthreyjteykjneirngweingsjdnукпуцпvsjdnvwrg");
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для описания");
    }

    @Test
    public void releaseDateTooEarlyTest() {
        Film film = Film.builder().build();
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ValidationException.class, () -> {
            filmController.createFilm(film);
        });
    }

    @Test
    public void durationMustBePositiveTest() {
        Film film = Film.builder().build();
        film.setDuration(-1);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty(), "Должна быть ошибка валидации для длительности");
    }
}
