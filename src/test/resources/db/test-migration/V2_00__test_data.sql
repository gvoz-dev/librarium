INSERT INTO "Publishers" ("id", "name", "country")
VALUES
    ('faa30418-8b96-421c-aa5d-2c994c5da3ee', 'Test Publisher', 'World');

INSERT INTO "Authors" ("id", "name")
VALUES
    ('e23406b0-17b3-4563-b0b6-ed4cefd160fb', 'Test Author');

INSERT INTO "Books" ("id", "title", "publisherId")
VALUES
    (
        'bf341570-5fa2-4af8-999c-151988750efa',
        'Test Book',
        'faa30418-8b96-421c-aa5d-2c994c5da3ee'
    );

INSERT INTO "BooksAuthors" ("bookId", "authorId")
VALUES
    ('bf341570-5fa2-4af8-999c-151988750efa', 'e23406b0-17b3-4563-b0b6-ed4cefd160fb');

INSERT INTO "Users" ("id", "name", "email", "password", "role")
VALUES
    (
        'b1cf4de7-5900-499e-93ca-b4a8740435c2',
        'test',
        'test@example.com',
        'asd',
        'dev'
    );

INSERT INTO "UsersBooks" ("id", "userId", "bookId")
VALUES
    (
        '1026b080-30cc-4e64-9a5f-23329184e522',
        'b1cf4de7-5900-499e-93ca-b4a8740435c2',
        'bf341570-5fa2-4af8-999c-151988750efa'
    );

INSERT INTO "Comments" ("id", "userBookId", "text", "date")
VALUES
    (
        '05141bb7-9903-4811-ade1-bc80eadf2cbb',
        '1026b080-30cc-4e64-9a5f-23329184e522',
        'Test comment',
        '2024-06-09 20:28:15'
    );
