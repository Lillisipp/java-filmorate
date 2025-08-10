package ru.yandex.practicum.filmorate.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.Friendship;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer id;
    private String email;
    private String login;
    private String name;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate birthday;
//    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Set<Friendship> friends = new HashSet<>();

}