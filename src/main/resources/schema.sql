-- 1. Таблица пользователей
CREATE TABLE users (
  user_id     INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  email       VARCHAR(255)      NOT NULL UNIQUE,
  login       VARCHAR(255)      NOT NULL UNIQUE,
  name        VARCHAR(255)      NOT NULL,
  birthday    DATE              NOT NULL
);

-- 2. Справочник MPA-рейтингов
CREATE TABLE mpa_rating (
  mpa_id      INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name        VARCHAR(20)       NOT NULL UNIQUE  -- 'G','PG','PG-13','R','NC-17'
);

-- 3. Справочник жанров
CREATE TABLE genres (
  genre_id    INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name        VARCHAR(50)       NOT NULL UNIQUE  -- 'ACTION','COMEDY',...
);

-- 4. Таблица фильмов
CREATE TABLE films (
  film_id       INTEGER PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
  name          VARCHAR(255)      NOT NULL,
  description   VARCHAR(200)      NOT NULL,
  release_date  DATE              NOT NULL,
  duration_min  INTEGER           NOT NULL,
  mpa_id        INTEGER           NOT NULL,
  FOREIGN KEY (mpa_id) REFERENCES mpa_rating (mpa_id)
);

-- 5. Many-to-many фильм↔жанр
CREATE TABLE film_genres (
  film_id     INTEGER NOT NULL,
  genre_id    INTEGER NOT NULL,
  PRIMARY KEY (film_id, genre_id),
  FOREIGN KEY (film_id)  REFERENCES films   (film_id),
  FOREIGN KEY (genre_id) REFERENCES genres  (genre_id)
);

-- 6. Таблица дружбы пользователей
CREATE TABLE friendships (
  user_id       INTEGER NOT NULL,
  friend_id     INTEGER NOT NULL,
  status        VARCHAR(12) NOT NULL,        -- 'UNCONFIRMED' или 'CONFIRMED'
  requested_at  TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY   (user_id, friend_id),
  FOREIGN KEY (user_id)   REFERENCES users (user_id),
  FOREIGN KEY (friend_id) REFERENCES users (user_id)
);

-- 7. Таблица лайков фильмов
CREATE TABLE film_likes (
  user_id    INTEGER   NOT NULL,
  film_id    INTEGER   NOT NULL,
  liked_at   TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, film_id),
  FOREIGN KEY (user_id) REFERENCES users (user_id),
  FOREIGN KEY (film_id) REFERENCES films (film_id)
);
