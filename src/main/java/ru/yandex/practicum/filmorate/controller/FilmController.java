package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAllfilms() {
        log.info("Возвращаем коллекцию фильмов");
        return films.values();
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("Добавляеем новый фильм {}", film);
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма не верна - {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
        films.put(film.getId(), film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film newFilm) {
        log.info("Пробуем обновить фильм на {}", newFilm);
        if (newFilm.getId() == 0) {
            log.debug("id не корректен");
            throw new ValidationException("Должен быть указан корректный id");
        }
        if (exists(newFilm)) {
            log.debug("id найден, id={}", newFilm.getId());
            Film oldFilm = films.get(newFilm.getId());
            log.debug("Обновляем название на {}", newFilm.getName());
            oldFilm.setName(newFilm.getName());
            log.debug("Обновляем описание");
            oldFilm.setDescription(newFilm.getDescription());
            if (newFilm.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                log.debug("Невозможноая дата релиза {}", newFilm.getReleaseDate());
                throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
            }
            log.debug("Обновляем дату на {}", newFilm.getReleaseDate());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            log.debug("Обновляем длительность на {}", newFilm.getDuration());
            oldFilm.setDuration(newFilm.getDuration());
            log.info("Обновлен фильм {}", oldFilm);
            return oldFilm;
        } else {
            log.debug("Фильм с id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильм с id = " + newFilm.getId() + " не найден");
        }
    }

    private long getNextId() {
        log.info("Генеринуем новый id");
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private boolean exists(Film newFilm) {
        return films.containsValue(newFilm);
    }

}
