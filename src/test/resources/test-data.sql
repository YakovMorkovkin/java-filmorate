MERGE INTO mpa (id,mpa_name)
    VALUES (1,'G'),
           (2,'PG'),
           (3,'PG-13'),
           (4,'R'),
           (5,'NC-17');

MERGE INTO genre (id,genre_name)
    VALUES (1,'Комедия'),
           (2,'Драма'),
           (3,'Мультфильм'),
           (4,'Триллер'),
           (5,'Документальный'),
           (6,'Боевик');

MERGE INTO directors (id,director_name)
VALUES (1,'Стивен Спилберг'),
       (2,'Френсис Форд Коппола'),
       (3,'Джеймс Кэмерон'),
       (4,'Гай Ричи');

INSERT INTO users  (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES ('user1@mail.ru', 'login1','Alex','19830623'),
       ('user2@mail.ru', 'login2','Piter','19831123'),
       ('user3@mail.ru', 'login3','Rene','19830323'),
       ('user4@mail.ru', 'login4','Feofan','19830723'),
       ('user5@mail.ru', 'login5','Bob','19831223'),
       ('user6@mail.ru', 'login6','Karp','19830123'),
       ('user7@mail.ru', 'login7','Trifon','19830223'),
       ('user8@mail.ru', 'login8','Elpedifor','19831023'),
       ('user9@mail.ru', 'login9','Nifont','19830423'),
       ('user10@mail.ru', 'login10','Vlad','19830523'),
       ('user11@mail.ru', 'login11','Ivan','19830823'),
       ('user12@mail.ru', 'login12','Stepan','19830923');

INSERT INTO FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, MPA)
VALUES ('Terminator', 'Film about killer-machine','19830823',120,2),
       ('Snatch', 'Film about gypsies','19831123',120,3),
       ('Jaws', 'Film about killer shark','19830823',120,5);

INSERT INTO FILMS_GENRE (FILM_ID, GENRE_ID)
VALUES (1,6),
       (2,1),(2,6),
       (3,4);

INSERT INTO FILM_LIKES (FILM_ID, LIKED_BY)
VALUES ( SELECT id FROM films WHERE name = 'Snatch',SELECT id FROM users WHERE name = 'Piter'),
       ( SELECT id FROM films WHERE name = 'Snatch',SELECT id FROM users WHERE name = 'Alex'),
       ( SELECT id FROM films WHERE name = 'Snatch',SELECT id FROM users WHERE name = 'Rene'),
       ( SELECT id FROM films WHERE name = 'Jaws',SELECT id FROM users WHERE name = 'Alex'),
       ( SELECT id FROM films WHERE name = 'Jaws',SELECT id FROM users WHERE name = 'Rene'),
       ( SELECT id FROM films WHERE name = 'Terminator',SELECT id FROM users WHERE name = 'Rene');