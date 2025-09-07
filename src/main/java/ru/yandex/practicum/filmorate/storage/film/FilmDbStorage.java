package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage {

    private final RowMapper<MpaRating> mpaMapper = new RowMapper<MpaRating>() {
        @Override
        public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException {
            MpaRating mpa = new MpaRating();
            mpa.setId(rs.getLong("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }
    };

    private final RowMapper<Genre> genreMapper = new RowMapper<>() {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre mpa = new Genre();
            mpa.setId(rs.getLong("id"));
            mpa.setName(rs.getString("name"));
            return mpa;
        }
    };

    private static final String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, mpa_rating_id)" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String BATCH_INSERT_FILM_GENRE_QUERY = "MERGE INTO film_genres (film_id, genre_id) " +
            "KEY (film_id, genre_id) " +
            "VALUES (?, ?)";
    private static final String UPDATE_QUERY =
            "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa_rating_id = ?  WHERE id = ?";
    private static final String CHECK_EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM films WHERE id = ?)";
    private static final String CHECK_MPA_EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM mpa_ratings WHERE id = ?)";
    private static final String CHECK_GENRE_EXISTS_QUERY = "SELECT EXISTS(SELECT 1 FROM genres WHERE id = ?)";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_ALL_MPAS_QUERY = "SELECT * FROM mpa_ratings m ORDER BY m.id";
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres g ORDER BY g.id";
    private static final String FIND_GENRES_FOR_FILM_QUERY = "SELECT * FROM genres g JOIN film_genres fg " +
            "ON g.id = fg.genre_id WHERE fg.film_id = ?";
    private static final String FIND_LIKES_FOR_FILM_QUERY = "SELECT user_id FROM film_likes WHERE film_id = ?";
    private static final String FIND_MPA_BY_ID_QUERY = "SELECT * FROM mpa_ratings WHERE id = ?";
    private static final String INSERT_LIKE_QUERY =
            "INSERT INTO film_likes(film_id, user_id) VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM film_likes WHERE film_id = ? AND user_id = ?";
    private static final String FIND_POPULAR_FILMS_QUERY = "SELECT f.*, COUNT(fl.film_id)\n" +
            "FROM films f \n" +
            "JOIN film_likes fl ON f.id = fl.film_id\n" +
            "GROUP BY f.id \n" +
            "ORDER BY COUNT(fl.film_id) DESC\n" +
            "LIMIT ?;";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Film> getAllFilms() {
        List<Film> allFilms = findMany(FIND_ALL_QUERY);
        for (Film film : allFilms) {
            loadMpaAndGenresForFilm(film);
            loadLikesForFilm(film);
        }
        return allFilms;
    }

    @Override
    public List<MpaRating> getAllMpas() {
        return findManyWithMapper(FIND_ALL_MPAS_QUERY, mpaMapper);
    }

    @Override
    public List<Genre> getAllGenres() {
        return findManyWithMapper(FIND_ALL_GENRES_QUERY, genreMapper);
    }

    @Override
    public Optional<Film> getFilmById(long filmId) {
        return findOne(FIND_BY_ID_QUERY, filmId).map(film ->
        {
            loadMpaAndGenresForFilm(film);
            loadLikesForFilm(film);
            return film;
        });
    }

    @Override
    public boolean isMpaExists(Long id) {
        return jdbc.queryForObject(CHECK_MPA_EXISTS_QUERY, Boolean.class, id);
    }

    @Override
    public boolean isGenreExists(Long id) {
        return jdbc.queryForObject(CHECK_GENRE_EXISTS_QUERY, Boolean.class, id);
    }

    @Override
    public Optional<Genre> getGenreById(Long id) {
        return findOneWithMapper(FIND_GENRE_BY_ID_QUERY, genreMapper, id);
    }

    @Override
    public Optional<MpaRating> getMpaById(Long id) {
        return findOneWithMapper(FIND_MPA_BY_ID_QUERY, mpaMapper, id);
    }

    @Override
    public Film addFilm(Film film) {
        long id = insert(INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());
        film.setId(id);
        addGenres(id, film.getGenres());
        return film;
    }

    private void addGenres(long filmId, List<Genre> genres) {
        List<Object[]> batch = new ArrayList<>();
        if (genres != null) {
            for (Genre genre : genres) {
                if (genre.getId() != null) {
                    batch.add(new Object[]{filmId, genre.getId()});
                }
            }
        }
        jdbc.batchUpdate(BATCH_INSERT_FILM_GENRE_QUERY, batch);
    }

    @Override
    public void addLike(long filmId, long userId) {
        update(INSERT_LIKE_QUERY, filmId, userId);
    }

    @Override
    public void deleteLike(long filmId, long userId) {
        delete(DELETE_LIKE_QUERY, filmId, userId);
    }

    @Override
    public Film updateFilm(Film film) {
        update(UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId());
        return film;
    }

    private void loadMpaAndGenresForFilm(Film film) {
        if (film.getMpa() != null) {
            Optional<MpaRating> mpa = findOneWithMapper(FIND_MPA_BY_ID_QUERY, mpaMapper, film.getMpa().getId());
            film.setMpa(mpa.orElse(null));
        }
        List<Genre> genres = findManyWithMapper(FIND_GENRES_FOR_FILM_QUERY, genreMapper, film.getId());
        film.setGenres(genres);
    }

    private void loadLikesForFilm(Film film) {
        List<Long> likes = jdbc.query(FIND_LIKES_FOR_FILM_QUERY,
                (rs, rowNum) -> rs.getLong("user_id"),
                film.getId());
        film.setLikes(new HashSet<>(likes));
    }

    @Override
    public Collection<Film> getMostPopularFilms(int count) {
        return findMany(FIND_POPULAR_FILMS_QUERY, count);
    }

    @Override
    public boolean existsById(long filmId) {
        return jdbc.queryForObject(CHECK_EXISTS_QUERY, Boolean.class, filmId);
    }
}

