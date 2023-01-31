# java-filmorate
Template repository for Filmorate project.

# ER-review

![This is an image](https://github.com/YakovMorkovkin/java-filmorate/blob/develop/Filmorate%20.png)

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
FROM user_friends
WHERE user_id = UserId
AND confirmation IS TRUE;
```
**Получение списка друзей пользователя по UserId :**
```
SELECT friends_with       
FROM user_friends
WHERE user_id = UserId
AND confirmation IS TRUE;
```
**Получение списка общих друзей пользователей по UserId1 и UserId2 :**
```
SELECT friends_with       
FROM user_friends
WHERE user_id = UserId1
AND confirmation IS TRUE
AND friends_with IN (
SELECT friends_with       
FROM friends
WHERE user_id = UserId2
AND confirmation IS TRUE
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
SELECT *       
FROM films
WHERE id IN (
SELECT film_id
FROM film_likes
GROUP BY film_id
ORDER BY COUNT(liked_by) DESC
LIMIT(count)
)
```
