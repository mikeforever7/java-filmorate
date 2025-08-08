package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class User {
    private long id;

    @Builder.Default
    private Set<Long> friends = new HashSet<>();

    @NotNull
    @NotBlank
    @Builder.Default
    private String login = "user";

    @Builder.Default
    private String name = "name";

    @Email
    @NotBlank
    @Builder.Default
    private String email = "user@yandex.ru";

    @NotNull
    @Past
    @Builder.Default
    private LocalDate birthday = LocalDate.of(1985, 5, 5);
}
