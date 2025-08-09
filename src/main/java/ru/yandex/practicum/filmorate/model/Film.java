package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class Film {
    private long id;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @NotNull
    @NotBlank
    @Builder.Default
    private String name = "Название";

    @NotNull
    @Size(max = 200)
    @Builder.Default
    private String description = "Описание";

    @NotNull
    @Builder.Default
    private LocalDate releaseDate = LocalDate.of(1985, 12, 12);

    @NotNull
    @Positive
    @Builder.Default
    private int duration = 10;
}
