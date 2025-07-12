package ru.yandex.practicum.filmorate.storege;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
public class FilmStorageTest {

    @InjectMocks
    private InMemoryFilmStorage inMemoryFilmStorage;

    private Film createFilm(String name) {
        Film film0 = new Film();
        film0.setName(name);
        film0.setDescription("Description");
        film0.setDuration(java.time.Duration.ofMinutes(90));
        film0.setReleaseDate(LocalDate.of(2000, 1, 1));
        return film0;
    }

    @Test
    void shouldSaveAndGetFilm() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Description");
        film.setDuration(java.time.Duration.ofMinutes(90));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));

        inMemoryFilmStorage.save(film);
        Film saved = inMemoryFilmStorage.getFilmById(film.getId());
        assertThat(saved).isNotNull();
        assertThat(saved.getName()).isEqualTo("Film 1");

    }

    @Test
    void shouldUpdateFilm() {
        Film film = new Film();
        film.setName("Film 1");
        film.setDescription("Description");
        film.setDuration(java.time.Duration.ofMinutes(90));
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        inMemoryFilmStorage.save(film);

        film.setName("Updated");
        inMemoryFilmStorage.update(film);

        Film update = inMemoryFilmStorage.getFilmById(film.getId());
        assertThat(update.getName()).isEqualTo("Updated");

    }

    @Test
    void shouldAddLikeToFilm() {
        Film film = createFilm("Film with Like");
        inMemoryFilmStorage.save(film);

        inMemoryFilmStorage.likeFilm(film.getId(), 1);
        Film likedFilm = inMemoryFilmStorage.getFilmById(film.getId());

        assertThat(likedFilm.getLikes()).contains(1);
    }

    @Test
    void shouldRemoveLikeFromFilm() {
        Film film = createFilm("Film with Like");
        inMemoryFilmStorage.save(film);
        inMemoryFilmStorage.likeFilm(film.getId(), 1);

        inMemoryFilmStorage.removeLikeFilm(film.getId(), 1);
        Film updatedFilm = inMemoryFilmStorage.getFilmById(film.getId());

        assertThat(updatedFilm.getLikes()).doesNotContain(1);
    }

    @Test
    void shouldThrowExceptionWhenRemovingNonexistentLike() {
        Film film = createFilm("Film without Like");
        inMemoryFilmStorage.save(film);

        assertThatThrownBy(() -> inMemoryFilmStorage.removeLikeFilm(film.getId(), 123))
                .isInstanceOf(ConditionsNotMetException.class)
                .hasMessageContaining("Лайк от пользователя не найден");
    }

    @Test
    void shouldReturnTopLikedFilms() {
        Film film1 = createFilm("Film 1");
        Film film2 = createFilm("Film 2");
        Film film3 = createFilm("Film 3");


        inMemoryFilmStorage.save(film1);
        inMemoryFilmStorage.save(film2);
        inMemoryFilmStorage.save(film3);

        inMemoryFilmStorage.likeFilm(film1.getId(), 1);
        inMemoryFilmStorage.likeFilm(film1.getId(), 2);
        inMemoryFilmStorage.likeFilm(film2.getId(), 1);

        List<Film> top = inMemoryFilmStorage.topLikeFilm(2);

        assertThat(top).hasSize(2);
        assertThat(top.get(0)).isEqualTo(film1);
        assertThat(top.get(1)).isEqualTo(film2);
    }

}
