-- 1. Таблица пользователей
create table if not exists users (
  user_id     serial PRIMARY KEY,
  email       VARCHAR(255)      NOT NULL UNIQUE,
  login       VARCHAR(255)      NOT NULL UNIQUE,
  name        VARCHAR(255)      NOT NULL,
  birthday    DATE              NOT NULL
);

-- 2. Справочник MPA-рейтингов
create table if not exists mpa_rating (
  mpa_id      serial PRIMARY KEY,
  name        VARCHAR(20)       NOT NULL UNIQUE  -- 'G','PG','PG-13','R','NC-17'
);

-- 3. Справочник жанров
create table if not exists genres (
  genre_id    serial PRIMARY KEY,
  name        VARCHAR(50)       NOT NULL UNIQUE  -- 'ACTION','COMEDY',...
);

-- 4. Таблица фильмов
create table if not exists films (
  film_id       serial PRIMARY KEY,
  name          VARCHAR(255)      NOT NULL,
  description   VARCHAR(200)      NOT NULL,
  release_date  DATE              NOT NULL,
  duration_min  INTEGER           NOT NULL,
  mpa_id        INTEGER           NOT NULL,
  FOREIGN KEY (mpa_id) REFERENCES mpa_rating (mpa_id)
);

-- 5. Many-to-many фильм↔жанр
create table if not exists film_genres (
  film_id     int not null,
  genre_id    int not null,
  primary key (film_id, genre_id),
  foreign key (film_id)  references films   (film_id),
  foreign key (genre_id) references genres  (genre_id)
);

-- 6. Таблица дружбы пользователей
create table if not exists friendships (
  user_id       int not null,
  friend_id     int not null,
  status        varchar(12) not null,        -- 'UNCONFIRMED' или 'CONFIRMED'
  requested_at  timestamp   default current_timestamp,
  primary key   (user_id, friend_id),
  foreign key (user_id)   references users (user_id),
  foreign key (friend_id) references users (user_id)
);

-- 7. Таблица лайков фильмов
create table if not exists film_likes (
  user_id    int   not null,
  film_id    int   not null,
  liked_at   timestamp default current_timestamp,
  primary key (user_id, film_id),
  foreign key (user_id) references users (user_id),
  foreign key (film_id) references films (film_id)
);
