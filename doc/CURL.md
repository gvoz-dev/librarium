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

## Users

### HTTP-запрос для регистрации пользователя

```bash
curl -i -X POST \
-H 'Content-Type: application/json' \
-d '{"name":"libra","email":"example@mail.ru","password":"123"}' \
'http://127.0.0.1:8080/api/v1/registration'
```

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

## Publishers

```bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"name":"BHV","country":"Russia"}' http://127.0.0.1:8080/publishers
```

```bash
curl -i -X PATCH -H 'Content-Type: application/json' -d '{"id":"???","name":"BHV","country":"Russia"}' http://127.0.0.1:8080/publishers
```

```bash
curl -i -X DELETE "http://127.0.0.1:8080/publishers/???"
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
-d '{"name":"Roman","country":"Russia"}' \
'http://127.0.0.1:8080/api/v1/authors'
```

### HTTP-запрос для изменения автора

```bash
curl -i -X PUT \
-H 'Content-Type: application/json' \
-d '{"id":"???","name":"Gvozdev Roman","country":"Russia"}' \
'http://127.0.0.1:8080/api/v1/authors'
```

### HTTP-запрос для удаления автора

```bash
curl -i -X DELETE 'http://127.0.0.1:8080/api/v1/authors/???'
```

----

## Books

```bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"title":"test","publisher":{"name":"test","country":"Russia"},"author":{"name":"Roman","country":"Russia"}}' http://127.0.0.1:8080/books
```

```bash
curl -i -X PATCH -H 'Content-Type: application/json' -d '{"id":"???","title":"test","publisher":{"id":"???","name":"pub","country":"Russia"},"author":{"id":"???","name":"Roma","country":"Russia"}},' http://127.0.0.1:8080/books
```

```bash
curl -i -X DELETE "http://127.0.0.1:8080/books/???"
```

----

## Comments

```bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"text":"cool","isPrivate":true}' http://127.0.0.1:8080/comments/???/???
```
