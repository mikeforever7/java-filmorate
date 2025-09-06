package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final FilmService filmService;
    private static final Logger log = LoggerFactory.getLogger(MpaController.class);

    @Autowired
    public MpaController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<MpaRating> getAllMpas() {
        log.info("Возвращаем коллекцию MpaRating");
        return filmService.getAllMpas();
    }

    @GetMapping("/{id}")
    public MpaRating getMpaById(@PathVariable long id) {
        return filmService.getMpaById(id);
    }
}
