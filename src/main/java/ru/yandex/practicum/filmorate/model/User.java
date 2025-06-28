package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;

    @Email(message = "Электронная почта должна содержать символ '@'.")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\S*$")
    private String login;
    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
