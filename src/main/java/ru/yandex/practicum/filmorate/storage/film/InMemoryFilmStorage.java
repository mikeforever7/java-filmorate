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
