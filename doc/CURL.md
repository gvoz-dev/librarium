# Примеры команд cURL

> Вместо знаков <???> в запросах необходимо подставить нужное значение.
> Это может быть, например, уникальный идентификатор, имя или токен аутентификации.

## Login

### HTTP-запрос для получения токена аутентификации

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-d '{"email":"admin@example.com","password":"12345"}' \
'http://127.0.0.1:8080/api/v1/login'
```

----

## Registration

### HTTP-запрос для регистрации пользователя

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-d '{"name":"libra","email":"example@mail.ru","password":"123"}' \
'http://127.0.0.1:8080/api/v1/registration'
```

----

## Users

### HTTP-запрос для получения всех пользователей

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/users'
```

### HTTP-запрос для поиска пользователей по имени

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/users?name=???'
```

### HTTP-запрос для поиска пользователя по ID

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/users/???'
```

### HTTP-запрос для изменения пользователя

```bash
curl -i -X PUT \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"id":"???","name":"Johnny","email":"example@mail.ru","password":"qwe"}' \
'http://127.0.0.1:8080/api/v1/users'
```

### HTTP-запрос для удаления пользователя

```bash
curl -i -X DELETE \
-H 'X-JWT-Auth: ???' \
'http://127.0.0.1:8080/api/v1/users/???'
```

----

## Books

### HTTP-запрос для получения всех книг

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/books'
```

### HTTP-запрос для поиска книги по названию

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/books?title=???'
```

### HTTP-запрос для поиска книги по ID

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/books/???'
```

### HTTP-запрос для создания новой книги

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"title":"TestBook","publisher":{"name":"TestPub","country":"Russia"},"author":{"name":"TestAuthor","country":"Russia"}}' \
'http://127.0.0.1:8080/api/v1/books'
```

### HTTP-запрос для изменения книги

```bash
curl -i -X PUT \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"id":"???","title":"test"},' \
'http://127.0.0.1:8080/api/v1/books'
```

### HTTP-запрос для удаления книги

```bash
curl -i -X DELETE "http://127.0.0.1:8080/api/v1/books/???"
```

----

## Library

### HTTP-запрос для получения всех книг из библиотеки пользователя

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/library/???'
```

### HTTP-запрос для добавления книги в библиотеку пользователя

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
'http://127.0.0.1:8080/api/v1/library/???/add/???'
```

### HTTP-запрос для удаления книги из библиотеки пользователя

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
'http://127.0.0.1:8080/api/v1/library/???/delete/???'
```

----

## Progress

### HTTP-запрос для получения прогресса прочитанного

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/progress/???/???'
```

### HTTP-запрос для установки прогресса прочитанного

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"value":78}' \
'http://127.0.0.1:8080/api/v1/progress/???/???'
```

----

## Rating

### HTTP-запрос для получения среднего рейтинга книги

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/rating/???/avg'
```

### HTTP-запрос для получения пользовательского рейтинга

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/rating/???/???'
```

### HTTP-запрос для установки пользовательского рейтинга

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"value":5}' \
'http://127.0.0.1:8080/api/v1/rating/???/???'
```

----

## Comments

### HTTP-запрос для добавления комментария к книге

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"text":"cool","isPrivate":true}' \
'http://127.0.0.1:8080/comments/???/???'
```

----

## Authors

### HTTP-запрос для получения всех авторов

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/authors'
```

### HTTP-запрос для поиска авторов по имени

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/authors?name=???'
```

### HTTP-запрос для поиска автора по ID

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/authors/???'
```

### HTTP-запрос для создания нового автора

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"name":"Bjarne Stroustrup","country":"Denmark"}' \
'http://127.0.0.1:8080/api/v1/authors'
```

### HTTP-запрос для изменения автора

```bash
curl -i -X PUT \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"id":"???","name":"Bjarne Stroustrup","country":"USA"}' \
'http://127.0.0.1:8080/api/v1/authors'
```

### HTTP-запрос для удаления автора

```bash
curl -i -X DELETE \
-H 'X-JWT-Auth: ???' \
'http://127.0.0.1:8080/api/v1/authors/???'
```

----

## Publishers

### HTTP-запрос для получения всех издателей

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/publishers'
```

### HTTP-запрос для поиска издателей по имени

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/publishers?name=???'
```

### HTTP-запрос для поиска издателя по ID

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/publishers/???'
```

### HTTP-запрос для добавления издателя

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"name":"BHV","country":"Russia"}' \
'http://127.0.0.1:8080/api/v1/publishers'
```

### HTTP-запрос для изменения издателя

```bash
curl -i -X PUT \
-H 'Content-Type: application/json' \
-H 'X-JWT-Auth: ???' \
-d '{"id":"???","name":"BHV","country":"Russia"}' \
'http://127.0.0.1:8080/api/v1/publishers'
```

### HTTP-запрос для удаления издателя

```bash
curl -i -X DELETE \
-H 'X-JWT-Auth: ???' \
'http://127.0.0.1:8080/api/v1/publishers/???'
```

----
