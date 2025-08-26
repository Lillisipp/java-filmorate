package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.model.enums.FriendshipStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Friendship {
    private Integer friendId;
    private FriendshipStatus status;
}
