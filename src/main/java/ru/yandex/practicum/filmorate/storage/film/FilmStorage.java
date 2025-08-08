package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film getFilmById(long id);

    void addFilm(Film film);

    long getNextId();

    boolean exists(Film newFilm);
}
