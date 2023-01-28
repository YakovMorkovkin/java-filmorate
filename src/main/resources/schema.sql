DROP TABLE IF EXISTS FILMS_GENRE CASCADE;
DROP TABLE IF EXISTS FILM_LIKES CASCADE;
DROP TABLE IF EXISTS FILMS CASCADE;
DROP TABLE IF EXISTS GENRE CASCADE;
DROP TABLE IF EXISTS MPA CASCADE;
DROP TABLE IF EXISTS DIRECTORS CASCADE;
DROP TABLE IF EXISTS USER_FRIENDS CASCADE;
DROP TABLE IF EXISTS USERS CASCADE;
DROP TABLE IF EXISTS USER_EVENTS CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email    varchar(30) NOT NULL,
    login    varchar(30) NOT NULL,
    name     varchar(30),
    birthday date NOT NULL,
    CONSTRAINT IF NOT EXISTS users_key_unique UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS mpa
(
    id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    mpa_name varchar(20),
    CONSTRAINT IF NOT EXISTS mpa_key_unique UNIQUE(mpa_name)
);

CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         varchar(30) NOT NULL,
    description  varchar(200) NOT NULL,
    release_date date NOT NULL,
    duration     int NOT NULL,
    mpa          int,
    FOREIGN KEY (mpa) REFERENCES mpa (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT IF NOT EXISTS films_key_unique UNIQUE(name,description,release_date,duration,mpa)
);

CREATE TABLE IF NOT EXISTS directors
(
    id  INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    director_name varchar(30) NOT NULL,
    CONSTRAINT IF NOT EXISTS directors_key_unique UNIQUE(director_name)
);

CREATE TABLE IF NOT EXISTS films_director
(
    film_id  int NOT NULL,
    director_id int NOT NULL,
    PRIMARY KEY (film_id,director_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (director_id) REFERENCES directors (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT IF NOT EXISTS films_director_key_unique UNIQUE(film_id,director_id)
);

CREATE TABLE IF NOT EXISTS genre
(
    id    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar(20),
    CONSTRAINT IF NOT EXISTS genre_key_unique UNIQUE(genre_name)
);

CREATE TABLE IF NOT EXISTS films_genre
(
    film_id  int NOT NULL,
    genre_id int NOT NULL,
    PRIMARY KEY (film_id,genre_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genre (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT IF NOT EXISTS films_genre_key_unique UNIQUE(film_id,genre_id)
);

CREATE TABLE IF NOT EXISTS user_friends
(
    user_id      int,
    friends_with int,
    confirmation boolean,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (friends_with) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT IF NOT EXISTS user_friends_key_unique UNIQUE(user_id,friends_with,confirmation)
);

CREATE TABLE IF NOT EXISTS film_likes
(
    film_id  int,
    liked_by int,
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (liked_by) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT IF NOT EXISTS film_likes_key_unique UNIQUE(film_id,liked_by)
);

CREATE TABLE IF NOT EXISTS user_events
(
    time_of_event    bigint NOT NULL,
    user_id          int NOT NULL,
    event_type       varchar(10) NOT NULL,
    operation        varchar(10) NOT NULL,
    event_id         INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    entity_id        int NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT IF NOT EXISTS user_events_key_unique UNIQUE(time_of_event,user_id,event_type,operation,entity_id)
);
