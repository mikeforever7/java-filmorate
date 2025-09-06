package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
public class Genre {
    private Long id;
    private String name;

    public Genre() {
    }

    public Genre(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
