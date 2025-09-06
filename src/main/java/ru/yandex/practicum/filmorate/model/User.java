package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
public class User {
    private Long id;
    private Set<Long> friends = new HashSet<>();

    @NotNull
    @NotBlank
    private String login;

    private String name;

    @Email
    @NotBlank
    private String email;

    @NotNull
    @Past
    private LocalDate birthday;
}
