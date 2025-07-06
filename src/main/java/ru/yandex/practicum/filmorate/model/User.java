package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//@Getter
//@Setter
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private Integer id;

    @Email(regexp = ".*@.*", message = "Электронная почта должна содержать символ '@'.")
    @NotBlank(message = "Электронная почта не может быть пустой")
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\S*$", message = "Логин не может быть пустым и содержать пробелы.")
    private String login;
    private String name;

    @PastOrPresent(message = "Дата рождения не может быть в будущем.")
    private LocalDate birthday;

    private Set<Integer> friends;
}
