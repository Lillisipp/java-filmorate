-- ЧИСТИМ ТАБЛИЦЫ (важен порядок из-за FK)
SET REFERENTIAL_INTEGRITY FALSE;

TRUNCATE TABLE film_likes;
TRUNCATE TABLE film_genres;
TRUNCATE TABLE friendships;
TRUNCATE TABLE films;
TRUNCATE TABLE users;
TRUNCATE TABLE genres;
TRUNCATE TABLE mpa_rating;

SET REFERENTIAL_INTEGRITY TRUE;

-- СПРАВОЧНИКИ
MERGE INTO mpa_rating (mpa_id, name) KEY(mpa_id) VALUES
  (1,'G'),
  (2,'PG'),
  (3,'PG-13'),
  (4,'R'),
  (5,'NC-17');

MERGE INTO genres (genre_id, name) KEY(genre_id) VALUES
  (1,'Комедия'),
  (2,'Драма'),
  (3,'Боевик'),
  (4,'Триллер'),
  (5,'Фантастика'),
  (6,'Мультфильм');

-- ПОЛЬЗОВАТЕЛИ
MERGE INTO users (user_id, email, login, name, birthday) KEY(user_id) VALUES
  (1, 'u1@mail.com', 'u1', 'User One',   DATE '1990-01-01'),
  (2, 'u2@mail.com', 'u2', 'User Two',   DATE '1991-02-02'),
  (3, 'u3@mail.com', 'u3', 'User Three', DATE '1992-03-03');

-- ФИЛЬМЫ
MERGE INTO films (film_id, name, description, release_date, duration_min, mpa_id) KEY(film_id) VALUES
  (1, 'Film One',   'Test film #1', DATE '2000-01-01', 100, 1),
  (2, 'Film Two',   'Test film #2', DATE '2005-05-05', 120, 2);

-- ЖАНРЫ ФИЛЬМОВ (многие-ко-многим)
MERGE INTO film_genres (film_id, genre_id) KEY(film_id, genre_id) VALUES
  (1, 1),  -- Film One: Комедия
  (1, 2),  -- Film One: Драма
  (2, 2);  -- Film Two: Драма

-- ДРУЖБА (односторонняя заявка)
MERGE INTO friendships (user_id, friend_id) KEY(user_id, friend_id) VALUES
  (1, 2),
  (3, 1);

-- ЛАЙКИ ФИЛЬМОВ
MERGE INTO film_likes (user_id, film_id) KEY(user_id, film_id) VALUES
  (1, 1),
  (2, 1),
  (2, 2);

ALTER TABLE users       ALTER COLUMN user_id   RESTART WITH 1000;
ALTER TABLE films       ALTER COLUMN film_id   RESTART WITH 1000;
ALTER TABLE genres      ALTER COLUMN genre_id  RESTART WITH 1000;
ALTER TABLE mpa_rating  ALTER COLUMN mpa_id    RESTART WITH 1000;