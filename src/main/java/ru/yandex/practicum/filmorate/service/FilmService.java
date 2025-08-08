package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

    public Collection<Film> getAllFilms() {
        log.info("Возвращаем коллекцию фильмов");
        return filmStorage.getAllFilms();
    }

    public Film getFilmById(long id) {
        Film film = filmStorage.getFilmById(id);
        if (film == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return filmStorage.getFilmById(id);
    }

    public Film createFilm(Film film) {
        log.info("Добавляеем новый фильм {}", film);
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.debug("Дата релиза фильма не верна - {}", film.getReleaseDate());
            throw new ValidationException("дата релиза — не раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
        film.setLikes(new HashSet<>());
        filmStorage.addFilm(film);
        log.info("Добавлен фильм {}", film);
        return film;
    }

    public Film updateFilm(Film newFilm) {
        log.info("Пробуем обновить фильм на {}", newFilm);
        if (newFilm.getId() == 0) {
            log.debug("id не корректен");
            throw new ValidationException("Должен быть указан корректный id");
        }
        if (exists(newFilm)) {
            log.debug("id найден, id={}", newFilm.getId());
            Film oldFilm = filmStorage.getFilmById(newFilm.getId());
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

    public void addLike(long filmId, long userId) {
        log.debug("Пробуем добавить лайк к фильму {} от юзера {}", filmId, userId);
        Film film = getFilmById(filmId);
        userService.getUserById(userId); //Для проверки наличия
        film.getLikes().add(userId);
        log.debug("Лайк успешно добавлен {}", film);
    }

    public void deleteLike(long filmId, long userId) {
        log.debug("Пробуем удалить лайк к фильму {} от юзера {}", filmId, userId);
        Film film = getFilmById(filmId);
        userService.getUserById(userId); //Для проверки наличия
        film.getLikes().remove(userId);
        log.debug("Лайк успешно удален {}", film);
    }

    public List<Film> getMostPopularFilms(int count) {
        return getAllFilms().stream()
                .sorted((film1, film2) -> {
                    int likes1 = film1.getLikes().size();
                    int likes2 = film2.getLikes().size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .collect(Collectors.toList());
    }

    private boolean exists(Film newFilm) {
        return filmStorage.exists(newFilm);
    }

    public long getNextId() {
        log.info("Генеринуем новый id");
        return filmStorage.getNextId();
    }
}
