package ru.yandex.practicum.filmorate.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final LocalDate START_DATE = LocalDate.of(1895, 12, 28);

    private static final Logger log = LoggerFactory.getLogger(FilmService.class);

    //inMemoryFilmStorage   filmDbStorage  inMemoryUserStorage   userDbStorage
    @Autowired
    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Collection<Film> getAllFilms() {
        log.info("Возвращаем коллекцию фильмов");
        return filmStorage.getAllFilms();
    }

    public Collection<MpaRating> getAllMpas() {
        log.info("Возвращаем коллекцию Mpa");
        return filmStorage.getAllMpas();
    }

    public Collection<Genre> getAllGenres() {
        log.info("Возвращаем коллекцию жанров");
        return filmStorage.getAllGenres();
    }

    public Film getFilmById(long id) {
        return filmStorage.getFilmById(id).orElseThrow(() -> new NotFoundException("Фильм с ID " + id + " не найден"));
    }

    public MpaRating getMpaById(long id) {
        Optional<MpaRating> mpa = filmStorage.getMpaById(id);
        if (mpa.isEmpty()) {
            throw new NotFoundException("Mpa с ID " + id + " не найден");
        }
        return mpa.get();
    }

    public Genre getGenreById(long id) {
        Optional<Genre> genre = filmStorage.getGenreById(id);
        if (genre.isEmpty()) {
            throw new NotFoundException("Genre с ID " + id + " не найден");
        }
        return genre.get();
    }

    public Film createFilm(Film film) {
        log.info("Пытаемся добавить новый фильм {}", film);
        validateReleaseDate(film.getReleaseDate());
        log.info("Дата прошла валидацию");
        validateGenres(film.getGenres());
        log.info("Жанры прошли валидацию");
        validateMpa(film.getMpa());
        log.info("Mpa прошел валидацию");
        if (filmStorage instanceof InMemoryFilmStorage) {
            enrichFilmData(film);
        }
        filmStorage.addFilm(film);
        log.info("Добавлен фильм с id={}", film.getId());
        return film;
    }

    public Film updateFilm(Film newFilm) {
        log.info("Пробуем обновить фильм на {}", newFilm);
        if (newFilm.getId() < 1) {
            log.warn("id не корректен");
            throw new ValidationException("Должен быть указан корректный id");
        }
        log.info("id = {} найден", newFilm.getId());
        if (!filmStorage.existsById(newFilm.getId())) {
            log.debug("id={} не найден", newFilm.getId());
            throw new NotFoundException("Фильма с id=" + newFilm.getId() + " не найдено");
        }
        log.debug("id найден, id={}", newFilm.getId());
        if (newFilm.getReleaseDate() != null) {
            validateReleaseDate(newFilm.getReleaseDate());
            log.info("Новая дата прошла валидацию");
        }
        if (newFilm.getMpa() != null) {
            validateMpa(newFilm.getMpa());
            log.info("Новый mpa прошел валидацию");
        }
        return filmStorage.updateFilm(newFilm);
    }

    public void addLike(long filmId, long userId) {
        log.info("Пробуем добавить лайк к фильму {} от юзера {}", filmId, userId);
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильма с id =" + filmId + " не найдено");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователя с id =" + userId + " не найдено");
        }
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь с id = {} успешно лайкнул фильм с id = {}", userId, filmId);
    }

    public void deleteLike(long filmId, long userId) {
        log.info("Пробуем удалить лайк к фильму {} от юзера {}", filmId, userId);
        if (!filmStorage.existsById(filmId)) {
            throw new NotFoundException("Фильма с id =" + filmId + " не найдено");
        }
        if (!userStorage.existsById(userId)) {
            throw new NotFoundException("Пользователя с id =" + userId + " не найдено");
        }
        filmStorage.deleteLike(filmId, userId);
        log.info("Лайк от юзера с id= {} для фильма с id={} успешно удален", userId, filmId);
    }

    public Collection<Film> getMostPopularFilms(int count) {
        return filmStorage.getMostPopularFilms(count);
    }

    private void validateReleaseDate(LocalDate releaseDate) {
        if (releaseDate.isBefore(START_DATE)) {
            log.warn("Дата релиза фильма не верна - {}", releaseDate);
            throw new ValidationException("дата релиза — не раньше: " + START_DATE);
        }
    }

    private void validateGenres(List<Genre> genres) {
        log.info("Начали валидацию жанров");
        if (genres != null) {
            for (Genre genre : genres) {
                if (genre == null) continue;
                if (genre.getId() == null) {
                    throw new ValidationException("id жанра не может быть null");
                }
                if (!filmStorage.isGenreExists(genre.getId())) {
                    throw new NotFoundException("Жанр с ID " + genre.getId() + " не найден");
                }
            }
        }
    }

    private void validateMpa(MpaRating mpa) {
        log.info("Mpa проходит валидацию");
        if (mpa != null && !filmStorage.isMpaExists(mpa.getId())) {
            throw new NotFoundException("Mpa с ID " + mpa.getId() + " не найден");
        }
        log.info("Mpa = {}", mpa);
    }

    private void enrichFilmData(Film film) {
        if (film.getGenres() != null) {
            List<Genre> fullGenre = film.getGenres().stream()
                    .filter(Objects::nonNull)
                    .filter(g -> g.getId() != null)
                    .map(g -> filmStorage.getGenreById(g.getId()))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .distinct()
                    .collect(Collectors.toList());
            film.setGenres(fullGenre);
        }
        if (film.getMpa() != null) {
            MpaRating fullMpa = filmStorage.getMpaById(film.getMpa().getId()).orElse(null);
            film.setMpa(fullMpa); //До этого была валидация, null не боимся
        }
    }
}
