package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();

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

    public Film getFilmById(long id) {
        return films.get(id);
    }

    public void addFilm(Film film) {
        films.put(film.getId(), film);
    }

    public boolean exists(Film newFilm) {
        return films.containsValue(newFilm);
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
