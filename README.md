# java-filmorate
Template repository for Filmorate project.

![ER-diagramm.png](ER-diagramm.png)

## Примеры запросов:

1. Получение всех пользователей:
```
SELECT *
FROM user;
```

2. Получение всех фильмов:
```
SELECT *
FROM film;
```

3. Получение топ 10 наиболее популярных фильмов
```
SELECT film_id
FROM likes
GROUP BY film_id
ORDER BY COUNT(user_id) DESC
LIMIT 10;
```

# Добавлены возможности :

## 1. Удаление фильмов и пользователей:

`DELETE /users/{userId}` - 
удаляет пользователя по идентификатору

`DELETE /films/{filmId}` - 
удаляет фильм по идентификатору

## 2. Вывод самых популярных фильмов по жанру и годам:

`GET /films/popular?count={limit}&genreId={genreId}&year={year}` 

Возвращает список самых популярных фильмов указанного жанра за нужный год.

## 3. Функциональность "Отзывы":

`POST /reviews` - 
Добавление нового отзыва.

`PUT /reviews` - 
Редактирование уже имеющегося отзыва.

`DELETE /reviews/{id}` - 
Удаление уже имеющегося отзыва.

`GET /reviews/{id}` - 
Получение отзыва по идентификатору.

`GET /reviews?filmId={filmId}&count={count}`

Получение всех отзывов по идентификатору фильма, если фильм не указан то все. Если кол-во не указано то 10.

- `PUT /reviews/{id}/like/{userId}`  — пользователь ставит лайк отзыву.
- `PUT /reviews/{id}/dislike/{userId}`  — пользователь ставит дизлайк отзыву.
- `DELETE /reviews/{id}/like/{userId}`  — пользователь удаляет лайк/дизлайк отзыву.
- `DELETE /reviews/{id}/dislike/{userId}`  — пользователь удаляет дизлайк отзыву.

## 4. Функциональность "Общие фильмы" :

`GET /films/common?userId={userId}&friendId={friendId}`

Возвращает список фильмов, отсортированных по популярности.

**Параметры**

`userId` — идентификатор пользователя, запрашивающего информацию;

`friendId` — идентификатор пользователя, с которым необходимо сравнить список фильмов.

## 5. Функциональность "Поиск":

`GET /fimls/search`

Возвращает список фильмов, отсортированных по популярности.

**Параметры строки запроса**

`query` — текст для поиска

`by` — может принимать значения `director` (поиск по режиссёру), `title` (поиск по названию), либо оба значения через запятую при поиске одновременно и по режиссеру и по названию.

**Пример**

`GET /films/search?query=крад&by=director,title`

## 6. Функциональность "Лента событий":

`GET /users/{id}/feed`

Возвращает ленту событий пользователя.

## 7. Функциональность "Рекомендации": 

`GET /users/{id}/recommendations`

Возвращает рекомендации по фильмам для просмотра.

## 8. Добавление режиссёров в фильмы:

`GET /films/director/{directorId}?sortBy=[year,likes]`

Возвращает список фильмов режиссера отсортированных по количеству лайков или году выпуска. 

`GET /directors` - Список всех режиссёров

`GET /directors/{id}`- Получение режиссёра по id

`POST /directors` - Создание режиссёра

`PUT /directors` - Изменение режиссёра

`DELETE /directors/{id}` - Удаление режиссёра