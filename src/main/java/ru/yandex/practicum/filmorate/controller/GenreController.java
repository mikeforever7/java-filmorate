package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/genres")
public class GenreController {
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(GenreController.class);

    @Autowired
    public GenreController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<Genre> getAllGenres() {
        log.info("Возвращаем список всех жанров");
        return filmService.getAllGenres();
    }

    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable long id) {
        return filmService.getGenreById(id);
    }
}
