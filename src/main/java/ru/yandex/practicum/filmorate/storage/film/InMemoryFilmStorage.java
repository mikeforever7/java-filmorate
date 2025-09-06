package ru.yandex.practicum.filmorate.storage.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(InMemoryFilmStorage.class);

    private static final Map<Long, Genre> GENRES = Map.of(
            1L, new Genre(1L, "Комедия"),
            2L, new Genre(2L, "Драма"),
            3L, new Genre(3L, "Мультфильм"),
            4L, new Genre(4L, "Триллер"),
            5L, new Genre(5L, "Документальный"),
            6L, new Genre(6L, "Боевик")
    );

    public static final Map<Long, MpaRating> MPAS = Map.of(
            1L, new MpaRating(1L, "G"),
            2L, new MpaRating(2L, "PG"),
            3L, new MpaRating(3L, "PG-13"),
            4L, new MpaRating(4L, "R"),
            5L, new MpaRating(5L, "NC-17"));

    public boolean isGenreExists(Long id) {
        return GENRES.containsKey(id);
    }

    public Optional<Genre> getGenreById(Long id) {
        return Optional.ofNullable(GENRES.get(id));
    }

    public boolean isMpaExists(Long id) {
        return MPAS.containsKey(id);
    }

    public List<MpaRating> getAllMpas() {
        return MPAS.values().stream()
                .sorted(Comparator.comparing(MpaRating::getId))
                .collect(Collectors.toList());
    }

    public List<Genre> getAllGenres() {
        return GENRES.values().stream()
                .sorted(Comparator.comparing(Genre::getId))
                .toList();
    }

    public Optional<MpaRating> getMpaById(Long id) {
        return Optional.ofNullable(MPAS.get(id));
    }

    public Collection<Film> getAllFilms() {
        return films.values();
    }

    public Collection<Film> getMostPopularFilms(int count) {
        return films.values().stream()
                .sorted((film1, film2) -> {
                    int likes1 = film1.getLikes().size();
                    int likes2 = film2.getLikes().size();
                    return Integer.compare(likes2, likes1);
                })
                .limit(count)
                .toList();
    }

    public Optional<Film> getFilmById(long id) {
        return Optional.ofNullable(films.get(id));
    }

    public Film addFilm(Film film) {
        film.setId(getNextId());
        films.put(film.getId(), film);
        return film;
    }

    public Film updateFilm(Film newFilm) {
        Film oldFilm = films.get(newFilm.getId());
        if (newFilm.getName() != null) {
            log.debug("Обновляем название на {}", newFilm.getName());
            oldFilm.setName(newFilm.getName());
        }
        if (newFilm.getDescription() != null) {
            log.debug("Обновляем описание");
            oldFilm.setDescription(newFilm.getDescription());
        }
        if (newFilm.getReleaseDate() != null) {
            log.debug("Обновляем дату на {}", newFilm.getReleaseDate());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
        }
        if (newFilm.getDuration() != null) {
            log.debug("Обновляем длительность на {}", newFilm.getDuration());
            oldFilm.setDuration(newFilm.getDuration());
        }
        if (newFilm.getMpa().getId() != null) {
            log.debug("Обновляем mpa на {}", newFilm.getMpa().getId());
            oldFilm.setMpa(MPAS.get(newFilm.getMpa().getId()));
        }
        return newFilm;
    }

    @Override
    public void addLike(long filmId, long userId) {
        films.get(filmId).getLikes().add(userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        films.get(filmId).getLikes().remove(userId);
    }

    public boolean existsById(long filmId) {
        return films.containsKey(filmId);
    }

    public long getNextId() {
        long currentMaxId = films.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
