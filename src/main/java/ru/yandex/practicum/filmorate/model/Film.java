package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.*;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Long id;
    private MpaRating mpa;
    private List<Genre> genres = new ArrayList<>();
    private Set<Long> likes = new HashSet<>();

    @NotNull
    @NotBlank
    private String name;

    @NotNull
    @Size(max = 200)
    private String description;

    @NotNull
    private LocalDate releaseDate;

    @NotNull
    @Positive
    private Integer duration;
}
