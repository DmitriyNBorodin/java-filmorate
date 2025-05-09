# Back-end сервиса поиска и оценки фильмов.

 Приложение хранит информацию о фильмах и пользователях в базе данных. Позволяет сохранять и получать данные о фильмах и пользователях, также ставить фильмам оценки.

## Использованные технологии:
- Spring Boot
- H2
- Docker
- Lombok
- Maven
- JDBC
- SQL

## Эндпойнты:
- получение списка всех фильмов GET /films
- получение информации об одном фильме GET /films/{film_id}
- сохранение информации о фильме POST /films
```
{
    "name":String
    "description":String
    "releaseDate":LocalDate
    "duration":Integer
    "genres":{{"id":Integer}, {"id":Integer}}
    "mpa":{"id":Integer}
}
```
- обновление информации о фильме PUT /films
- добавление лайка к фильму PUT /films/{film_id}/likes/{user_id}
- удаление лайка к фильму DELETE /films/{film_id}/likes/{user_id}
- получание списка фильмов с наибольшим количеством лайков GET /films/popular?Integer
```
    films_number
```
- получение списка всех пользователей GET /users
- добавление нового пользователя POST /users
```
    "email":String
    "login":String
    "name":String
    "birthday":LocalDate
```
- обновление информации о пользователе PUT /users
- добавление друга PUT /users/{userId}/friends/{friendId}
- удаление друга DELETE /users/{userId}/friends/{friendId}
- получение списка друзей GET /users/{userId}/friends
- получение списка общих друзей GET /users/{userId}/friends/common/{anotherUserId}

Схема базы данных:
https://dbdiagram.io/d/Filmorate-66ddac54eef7e08f0e0b0ea4

Приложение является частью группового проекта, полная версия расположена по адресу:
https://github.com/EyesHead/java-filmorate
