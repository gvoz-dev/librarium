INSERT INTO "Publishers" ("id", "name", "country")
VALUES
    ('37d706ed-9591-4fd3-8811-9970194347da', 'Artima', 'USA'),
    ('b43e5b87-a042-461b-8728-653eddced002', 'Addison-Wesley', 'USA'),
    ('4c007df8-4c12-435b-9c1d-082e204db21e', 'Наука', 'СССР');

INSERT INTO "Authors" ("id", "name", "country")
VALUES
    ('7a7713e0-a518-4e3a-bf8f-bc984150a3b4', 'Martin Odersky', 'Germany'),
    ('0584125f-74e9-4b2b-92e2-e7396803aaba', 'Donald Knuth', 'USA'),
    ('3d51a46c-d627-4123-bdb4-4af1a0bfd5ee', 'Колмогоров А.Н.', 'СССР');

INSERT INTO "Books" ("id", "title", "isbn13", "year", "pages", "description", "language", "category", "publisherId")
VALUES
    (
        'b43e5b87-a042-461b-8728-653eddced002',
        'Scala. Профессиональное программирование',
        '9785446119141',
        2022,
        608,
        'Главная книга по Scala, популярному языку для платформы Java, в котором сочетаются концепции ООП и ФП.',
        'RU',
        'programming',
        '37d706ed-9591-4fd3-8811-9970194347da'
    ),
    (
        'eb98fd47-793e-448c-ad50-0a68d1f76252',
        'Теория вероятностей и математическая статистика',
        null,
        1986,
        535,
        'Настоящее издание представляет собой вторую книгу избранных трудов А.Н. Колмогорова.',
        'RU',
        'math',
        '4c007df8-4c12-435b-9c1d-082e204db21e'
    );

INSERT INTO "BooksAuthors" ("bookId", "authorId")
VALUES
    ('b43e5b87-a042-461b-8728-653eddced002', '7a7713e0-a518-4e3a-bf8f-bc984150a3b4'),
    ('eb98fd47-793e-448c-ad50-0a68d1f76252', '3d51a46c-d627-4123-bdb4-4af1a0bfd5ee');

INSERT INTO "Users" ("id", "name", "email", "password", "role")
VALUES
    (
        'ea962bb3-8f66-4256-bea5-8851c8f37dfb',
        'admin',
        'admin@example.com',
        -- SHA-256 для пароля 12345
        '5994471abb01112afcc18159f6cc74b4f511b99806da59b3caf5a9c173cacfc5',
        'admin'
    ),
    (
        'ca3e509d-06cf-4655-802a-7f8355339e2c',
        'roman',
        'roman@example.com',
        -- SHA-256 для пароля qwe
        '489cd5dbc708c7e541de4d7cd91ce6d0f1613573b7fc5b40d3942ccb9555cf35',
        'user'
    );

INSERT INTO "UsersBooks" ("id", "userId", "bookId", "inLibrary", "rating", "progress")
VALUES
    (
        'af1add3d-fc76-4ca0-ae99-5194c65f9af5',
        'ca3e509d-06cf-4655-802a-7f8355339e2c',
        'b43e5b87-a042-461b-8728-653eddced002',
        true,
        5,
        75.0
    );

INSERT INTO "Comments" ("id", "userBookId", "text", "date")
VALUES
    (
        '3bde6a1f-3eb7-4f36-b4c0-422ab4f39e99',
        'af1add3d-fc76-4ca0-ae99-5194c65f9af5',
        'Отличная книга, рекомендую!',
        '2024-06-09 20:28:15'
    );
