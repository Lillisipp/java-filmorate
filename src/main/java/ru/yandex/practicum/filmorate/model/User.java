package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class User {
    Integer id;

    @Email
    @NotBlank
    String email;

    @NotBlank
    @Pattern(regexp = "^\\S*$")
    String login;
    String name;

    @PastOrPresent
    LocalDate birthday;
}
