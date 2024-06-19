-- Издательства
CREATE TABLE IF NOT EXISTS "Publishers"
(
    "id" uuid NOT NULL,
    "name" text NOT NULL,
    "country" text NOT NULL,
    -- в будущем можно перенести "страну" в отдельную таблицу
    -- в будущем можно добавить "описание" и "логотип" издателя

    CONSTRAINT "PublisherPK" PRIMARY KEY ("id")
);

-- Авторы
CREATE TABLE IF NOT EXISTS "Authors"
(
    "id" uuid NOT NULL,
    "name" text NOT NULL,
    "country" text,
    -- в будущем можно перенести "страну" в отдельную таблицу
    -- в будущем можно добавить "биографию" и "портрет" автора

    CONSTRAINT "AuthorPK" PRIMARY KEY ("id")
);

-- Книги
CREATE TABLE IF NOT EXISTS "Books"
(
    "id" uuid NOT NULL,
    "title" text NOT NULL,
    "isbn" character varying(10),
    "isbn13" character varying(13),
    "edition" text,
    "year" integer,
    "pages" integer,
    "image" text,
    "description" text,
    "language" text,
    "category" text,
    "publisherId" uuid,
    -- в будущем "язык" и "категорию(-ии)" можно перенести в отдельные таблицы

    CONSTRAINT "BookPK" PRIMARY KEY ("id"),
    CONSTRAINT "PublisherFK" FOREIGN KEY ("publisherId")
        REFERENCES "Publishers" ("id") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

-- Книги-Авторы
CREATE TABLE IF NOT EXISTS "BooksAuthors"
(
    "bookId" uuid NOT NULL,
    "authorId" uuid NOT NULL,

    CONSTRAINT "BookAuthorPK" PRIMARY KEY ("bookId", "authorId"),
    CONSTRAINT "AuthorFK" FOREIGN KEY ("authorId")
        REFERENCES "Authors" ("id") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT "BookFK" FOREIGN KEY ("bookId")
        REFERENCES "Books" ("id") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- Пользователи
CREATE TABLE IF NOT EXISTS "Users"
(
    "id" uuid NOT NULL,
    "name" text NOT NULL,
    "email" character varying(320) NOT NULL UNIQUE,
    "password" text NOT NULL,
    "role" text NOT NULL,
    -- в будущем можно добавить "аватар" пользователя
    -- в будущем можно перенести "роль" в отдельную таблицу

    CONSTRAINT "UsersPK" PRIMARY KEY ("id")
);

-- Пользователи-Книги
CREATE TABLE IF NOT EXISTS "UsersBooks"
(
    "id" uuid NOT NULL,
    "userId" uuid NOT NULL,
    "bookId" uuid NOT NULL,
    "inLibrary" boolean DEFAULT FALSE,
    "rating" integer,
    "progress" real,

    CONSTRAINT "UserBookPK" PRIMARY KEY ("id"),
    CONSTRAINT "UserFK" FOREIGN KEY ("userId")
        REFERENCES "Users" ("id") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT "BookFK" FOREIGN KEY ("bookId")
        REFERENCES "Books" ("id") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CHECK (("rating" IS NULL) OR ("rating" BETWEEN 0 AND 5)),
    CHECK (("progress" IS NULL) OR ("progress" BETWEEN 0 AND 100))
);

-- Комментарии
CREATE TABLE IF NOT EXISTS "Comments"
(
    "id" uuid NOT NULL,
    "userBookId" uuid NOT NULL,
    "text" text NOT NULL,
    "isPrivate" boolean DEFAULT FALSE,
    "date" timestamp NOT NULL,
    -- в будущем можно добавить "время последнего изменения"

    CONSTRAINT "CommentPK" PRIMARY KEY ("id"),
    CONSTRAINT "UserBookFK" FOREIGN KEY ("userBookId")
        REFERENCES "UsersBooks" ("id") MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
