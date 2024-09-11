DROP TABLE IF EXISTS mpa_codes, films, genre_codes, film_genre, friend_status, users, user_friends, film_likes;

CREATE TABLE IF NOT EXISTS mpa_codes (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    code_name varchar
);

CREATE TABLE IF NOT EXISTS films (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name varchar NOT NULL,
    description varchar,
    release_date date,
    duration INTEGER,
    mpa_rating INTEGER REFERENCES mpa_codes (id)
);

CREATE TABLE IF NOT EXISTS genre_codes (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    genre_name varchar
);

CREATE TABLE IF NOT EXISTS film_genre (
    film_id INTEGER REFERENCES films (id),
    genre_id INTEGER REFERENCES genre_codes (id),
    PRIMARY KEY (film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS friend_status (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    status_name varchar
);

CREATE TABLE IF NOT EXISTS users (
    id INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    email varchar,
    login varchar,
    name varchar,
    birthdate date
);

CREATE TABLE IF NOT EXISTS user_friends (
    user_id INTEGER REFERENCES users (id),
    friend_id INTEGER REFERENCES users (id),
    request_status INTEGER REFERENCES friend_status (id),
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INTEGER REFERENCES films (id),
    user_id INTEGER REFERENCES users (id),
    PRIMARY KEY (film_id, user_id)
);