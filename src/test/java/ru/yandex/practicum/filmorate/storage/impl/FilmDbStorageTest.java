package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.model.enums.MPA;
import ru.yandex.practicum.filmorate.repository.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.MpaRatingRowMapper;
import ru.yandex.practicum.filmorate.repository.mappers.UserRowMapper;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;


@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, UserDbStorage.class, FilmRowMapper.class, UserRowMapper.class,
        GenreRowMapper.class,
        MpaRatingRowMapper.class})
class FilmDbStorageTest {
    private final FilmDbStorage filmDbStorage;
    private final UserDbStorage userDbStorage;

    private static final MpaRating G = new MpaRating(1, MPA.G);

    private Film newFilm(String name) {
        return new Film(
                null,
                name,
                "desc",
                LocalDate.of(2000, 1, 1),
                Duration.ofMinutes(100),
                new HashSet<>(),       // likes
                Set.<Genre>of(),
                G
        );
    }

    @Test
    @DisplayName("Поиск фильма по ID возвращает правильный фильм")
    void testFindFilmById() {
        assertThat(filmDbStorage.getFilmById(1))
                .isPresent()
                .hasValueSatisfying(f -> assertAll(
                        () -> assertThat(f.getId()).isEqualTo(1),
                        () -> assertThat(f.getName()).isEqualTo("Film One"),
                        () -> assertThat(f.getDescription()).isEqualTo("Test film #1"),
                        () -> assertThat(f.getDuration().toMinutes()).isEqualTo(100),
                        () -> assertThat(f.getReleaseDate()).isEqualTo(LocalDate.of(2000, 1, 1)),
                        () -> assertThat(f.getMpa().getId()).isEqualTo(1)
                ));
    }


    @Test
    @DisplayName("save → getById: ID присваивается, запись читается")
    void save_and_getById() {
        var f = filmDbStorage.save(newFilm("F1"));
        assertThat(f.getId()).isNotNull();

        var fromDb = filmDbStorage.getFilmById(f.getId());
        assertThat(fromDb).isPresent();
        assertThat(fromDb.get().getName()).isEqualTo("F1");
    }


    @Test
    @DisplayName("update изменяет поля фильма")
    void update_film() {
        Film f = filmDbStorage.save(newFilm("Old"));
        f.setName("New");
        f.setDescription("new desc");
        f.setDuration(Duration.ofMinutes(123));

        Film updated = filmDbStorage.update(f);

        filmDbStorage.getFilmById(updated.getId()).ifPresent(db -> assertAll(
                () -> assertThat(db.getName()).isEqualTo("New"),
                () -> assertThat(db.getDescription()).isEqualTo("new desc"),
                () -> assertThat(db.getDuration().toMinutes()).isEqualTo(123)
        ));
    }

    @Test
    @DisplayName("getFilms возвращает все фильмы")
    void get_all_films() {

        var all = filmDbStorage.getFilms();
        assertThat(all).hasSize(2);
        assertThat(all).extracting("name").containsExactlyInAnyOrder("Film One", "Film Two");
    }

    @Test
    @Sql(statements = "DELETE FROM film_likes", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @DisplayName("likeFilm / removeLikeFilm работают; topLikeFilm сортирует по лайкам")
    void like_unlike_and_top() {
        var u1 = userDbStorage.getUserById(1).get().getId();
        var u2 = userDbStorage.getUserById(2).get().getId();

        var f1 = filmDbStorage.save(newFilm("F1"));
        var f2 = filmDbStorage.save(newFilm("F2"));
        var f3 = filmDbStorage.save(newFilm("F3"));

        filmDbStorage.likeFilm(f1.getId(), u1);
        filmDbStorage.likeFilm(f1.getId(), u2);
        filmDbStorage.likeFilm(f2.getId(), u1);

        List<Film> top2 = filmDbStorage.topLikeFilm(2);
        assertThat(top2).extracting(Film::getName).containsExactly("F1", "F2");

        filmDbStorage.removeLikeFilm(f1.getId(), u2);

        List<Film> topAll = filmDbStorage.topLikeFilm(10);
        assertThat(topAll).extracting(Film::getName).contains("F1", "F2");
    }

    @Test
    @DisplayName("getGeners: возвращает все жанры в порядке id")
    void getGeners_All() {
        List<Genre> genres = filmDbStorage.getGeners();
        assertThat(genres).hasSize(6);
        assertThat(genres.get(0).getId()).isEqualTo(1);
        assertThat(genres.get(0).getType().getName()).isEqualTo("Комедия");
        assertThat(genres.get(1).getType().getName()).isEqualTo("Драма");
    }

    @Test
    @DisplayName("getGenerById: находит жанр по id")
    void getGenerById_returnsOne() {
        var opt = filmDbStorage.getGenerById(2);
        assertThat(opt).isPresent();
        assertThat(opt.get().getType().name()).isEqualTo("DRAMA");
    }

    @Test
    @DisplayName("getMpa: возвращает все рейтинги в порядке id")
    void getMpa_returnsAll() {
        List<MpaRating> all = filmDbStorage.getMpa();
        assertThat(all).hasSize(5);
        assertThat(all).extracting(MpaRating::getId)
                .containsExactly(1, 2, 3, 4, 5);
        assertThat(all).extracting(m -> m.getMpa().name())
                .containsExactly("G", "PG", "PG13", "R", "NC17");
    }

    @Test
    @DisplayName("getMPAById: находит рейтинг по id")
    void getMPAById_returnsOne() {
        var opt = filmDbStorage.getMPAById(1);
        assertThat(opt).isPresent();
        assertThat(opt.get().getMpa().name()).isEqualTo("G");
    }
}
