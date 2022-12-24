# java-filmorate
Template repository for Filmorate project.

# ER-review

![This is an image](https://github.com/YakovMorkovkin/java-filmorate/blob/main/Filmorate.png)

# Примеры SQL - запросов

**Получение всех пользователей :**
```
SELECT *       
FROM users 
GROUP BY id;
```

**Получение пользователя по userId :**
```
SELECT *       
FROM users 
WHERE id = UserId;
```
**Получение списка друзей пользователя по UserId :**
```
SELECT *       
FROM friends
WHERE user1_id = UserId
AND confirmation = 'true';
```
**Получение списка друзей пользователя по UserId :**
```
SELECT user2_id       
FROM friends
WHERE user1_id = UserId
AND confirmation = TRUE;
```
**Получение списка общих друзей пользователей по UserId1 и UserId2 :**
```
SELECT user2_id       
FROM friends
WHERE user1_id = UserId1
AND confirmation = TRUE
AND user2_id IN (
SELECT user2_id       
FROM friends
WHERE user1_id = UserId2
AND confirmation = TRUE
)
```
**Получение всех фильмов :**
```
SELECT *       
FROM films 
GROUP BY id;
```
**Получение фильма по filmId :**
```
SELECT *       
FROM films
WHERE id = filmId;
```
**Получение заданного (count) количества самых популярных фильмов :**
```
SELECT id       
FROM films
WHERE id IN (
SELECT film_id
FROM likes
GROUP BY film_id
ORDER BY COUNT(user_id) desc
LIMIT(count)
)
```
