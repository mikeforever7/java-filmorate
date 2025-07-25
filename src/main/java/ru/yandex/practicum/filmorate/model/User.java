package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
@Builder
public class User {
    private long id;

    @Email
    @NotBlank
    @Builder.Default
    private String email = "user@yandex.ru";

    @NotNull
    @NotBlank
    @Builder.Default
    private String login = "user";

    @Builder.Default
    private String name = "name";

    @NotNull
    @Past
    @Builder.Default
    private LocalDate birthday = LocalDate.of(1985, 5, 5);
}
