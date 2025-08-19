package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
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
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
//    @JsonIgnore
    private Set<Friendship> friends = new HashSet<>();

}