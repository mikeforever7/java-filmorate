package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Collection<MpaRating> getAllMpas();

    Collection<Genre> getAllGenres();

    Collection<Film> getMostPopularFilms(int count);

    Optional<Film> getFilmById(long id);

    Optional<Genre> getGenreById(Long id);

    Optional<MpaRating> getMpaById(Long id);

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void addLike(long filmId, long userId);

    void deleteLike(long filmId, long userId);

    boolean existsById(long id);

    boolean isGenreExists(Long id);

    boolean isMpaExists(Long id);
}
