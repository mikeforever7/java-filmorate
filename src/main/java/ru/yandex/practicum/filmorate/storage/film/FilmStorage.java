package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Collection<Film> getMostPopularFilms(int count);

    Film getFilmById(long id);

    Film addFilm(Film film);

    boolean exists(Film newFilm);

}
