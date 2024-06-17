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

---

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

---

## Authors

### HTTP-запрос для получения всех авторов

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/authors'
```

### HTTP-запрос для получения авторов по имени

```bash
curl -i -X GET 'http://127.0.0.1:8080/api/v1/authors?name=???'
```

### HTTP-запрос для получения автора по ID

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

---

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

---

## Users

```bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"name":"itcube","email":"example@mail.ru","password":"123","role":"user"}' http://127.0.0.1:8080/users
```

```bash
curl -i -X PATCH -H 'Content-Type: application/json' -d '{"id":"???","name":"itcube46","email":"example@mail.ru","password":"qwe"}' http://127.0.0.1:8080/users
```

```bash
curl -i -X DELETE "http://127.0.0.1:8080/users/???"
```

---

## Comments

```bash
curl -i -X POST -H 'Content-Type: application/json' -d '{"text":"cool","isPrivate":true}' http://127.0.0.1:8080/comments/???/???
```
