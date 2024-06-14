package itcube.repositories.book

import io.getquill.*
import itcube.entities.{Author, Book, Publisher}
import itcube.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource
import scala.util.Try

/** Реализация репозитория книг для СУБД PostgreSQL.
  *
  * @param ds
  *   источник данных [[DataSource]]
  */
case class PgBookRepository(ds: DataSource) extends BookRepository:

  import PostgresContext.*

  private val dsLayer: ULayer[DataSource] = ZLayer.succeed(ds)

  /** Преобразование строки таблицы в сущность "Книга". */
  private inline def toBook
      : ((Books, Option[Publishers], Option[Authors])) => Book =
    case (bookRow, publisherRow, authorRow) =>
      Book(
        Some(bookRow.id),
        bookRow.title,
        bookRow.isbn,
        bookRow.isbn13,
        bookRow.edition,
        bookRow.year,
        bookRow.pages,
        bookRow.image,
        bookRow.description,
        bookRow.language,
        bookRow.category,
        publisherRow.map(p => Publisher(Some(p.id), p.name, p.country)),
        authorRow.map(a => Author(Some(a.id), a.name, a.country))
      )

  /** Преобразование сущности "Книга" в строку таблицы.
    *
    * @param id
    *   уникальный идентификатор книги
    * @param book
    *   книга
    * @param publishers
    *   запись издателя книги, если есть
    */
  private inline def toBooksRow(
      id: UUID,
      book: Book,
      publishers: Option[Publishers]
  ): Books =
    lift(
      Books(
        id,
        book.title,
        book.isbn,
        book.isbn13,
        book.edition,
        book.year,
        book.pages,
        book.image,
        book.description,
        book.language,
        book.category,
        publishers.map(_.id)
      )
    )

  /** Получить все книги. */
  override def all: Task[List[Book]] =
    run {
      quote {
        for {
          books <- query[Books]
          publishers <- query[Publishers]
            .leftJoin(p => books.publisherId.contains(p.id))
          booksAuthors <- query[BooksAuthors]
            .leftJoin(ba => ba.bookId == books.id)
          authors <- query[Authors]
            .leftJoin(a => booksAuthors.exists(ba => ba.authorId == a.id))
        } yield (books, publishers, authors)
      }.map(toBook)
    }.provide(dsLayer)
  end all

  /** Получить книгу по ID.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  override def findById(id: String): Task[Option[Book]] =
    for {
      uuid <- ZIO.fromTry(Try(UUID.fromString(id)))
      result <- run {
        quote {
          for {
            books <- query[Books]
              .filter(b => b.id == lift(uuid))
            publishers <- query[Publishers]
              .leftJoin(p => books.publisherId.contains(p.id))
            booksAuthors <- query[BooksAuthors]
              .leftJoin(ba => books.id == ba.bookId)
            authors <- query[Authors]
              .leftJoin(a => booksAuthors.exists(ba => ba.authorId == a.id))
          } yield (books, publishers, authors)
        }.map(toBook)
      }.map(_.headOption).provide(dsLayer)
    } yield result
  end findById

  /** Получить книгу по названию.
    *
    * @param title
    *   название книги
    */
  override def findByTitle(title: String): Task[Option[Book]] =
    run {
      quote {
        for {
          books <- query[Books]
            .filter(b => b.title == lift(title))
          publishers <- query[Publishers]
            .leftJoin(p => books.publisherId.contains(p.id))
          booksAuthors <- query[BooksAuthors]
            .leftJoin(ba => books.id == ba.bookId)
          authors <- query[Authors]
            .leftJoin(a => booksAuthors.exists(ba => ba.authorId == a.id))
        } yield (books, publishers, authors)
      }.map(toBook)
    }.map(_.headOption).provide(dsLayer)
  end findByTitle

  /** Создать книгу.
    *
    * @param book
    *   книга
    */
  override def create(book: Book): Task[Book] =
    transaction {
      for {
        publishers <- createBookPublisher(book.publisher)
        books <- for {
          id <- Random.nextUUID
          result <- run {
            quote {
              query[Books]
                .insertValue(toBooksRow(id, book, publishers))
                .returning(r => r)
            }
          }
        } yield result
        authors <- createBookAuthor(book.author, books.id)
      } yield (books, publishers, authors)
    }.map(toBook).provide(dsLayer)
  end create

  /** Создать издателя новой книги:
    *   - издатель не представлен ([[None]]) => ничего не происходит,
    *   - издатель представлен ([[Some]]), но его нет в БД => создаётся запись в
    *     БД и возвращается,
    *   - издатель представлен ([[Some]]) и есть в БД => возвращается запись из
    *     БД.
    *
    * @param publisherOpt
    *   опциональный издатель
    */
  private def createBookPublisher(
      publisherOpt: Option[Publisher]
  ): Task[Option[Publishers]] =
    publisherOpt match
      case None => ZIO.none
      case Some(publisher) if publisher.id.isEmpty =>
        for {
          id <- Random.nextUUID
          result <- run {
            quote {
              query[Publishers]
                .insertValue(
                  lift(Publishers(id, publisher.name, publisher.country))
                )
                .returning(r => r)
            }
          }.option.provide(dsLayer)
        } yield result
      case Some(publisher) =>
        for {
          uuid <- ZIO.getOrFail(publisher.id)
          result <- run {
            quote {
              query[Publishers]
                .filter(p => p.id == lift(uuid))
            }
          }.map(_.headOption).provide(dsLayer)
        } yield result
  end createBookPublisher

  /** Создать автора новой книги:
    *   - автор не представлен ([[None]]) => ничего не происходит,
    *   - автор представлен ([[Some]]), но его нет в БД => создаётся запись в БД
    *     и возвращается,
    *   - автор представлен ([[Some]]) и есть в БД => возвращается запись из БД.
    *
    * Если автор представлен, то дополнительно создаётся соответствующая запись
    * в отношении "Книги-Авторы".
    *
    * @param authorOpt
    *   опциональный автор
    * @param bookId
    *   идентификатор книги
    */
  private def createBookAuthor(
      authorOpt: Option[Author],
      bookId: UUID
  ): Task[Option[Authors]] =
    authorOpt match
      case None => ZIO.none
      case Some(author) if author.id.isEmpty =>
        {
          for {
            id <- Random.nextUUID
            result <- run {
              quote {
                query[Authors]
                  .insertValue(lift(Authors(id, author.name, author.country)))
                  .returning(r => r)
              }
            }
            _ <- run {
              quote {
                query[BooksAuthors]
                  .insertValue(lift(BooksAuthors(bookId, result.id)))
              }
            }
          } yield result
        }.option.provide(dsLayer)
      case Some(author) =>
        {
          for {
            uuid <- ZIO.getOrFail(author.id)
            result <- run {
              quote {
                query[Authors]
                  .filter(a => a.id == lift(uuid))
              }
            }
            _ <- run {
              quote {
                query[BooksAuthors]
                  .insertValue(lift(BooksAuthors(bookId, uuid)))
              }
            }
          } yield result
        }.map(_.headOption).provide(dsLayer)
  end createBookAuthor

  /** Изменить книгу.
    *
    * @param book
    *   книга
    */
  override def update(book: Book): Task[Book] =
    transaction {
      for {
        publishers <- createBookPublisher(book.publisher)
        books <- for {
          id <- ZIO.getOrFail(book.id)
          result <- run {
            quote {
              query[Books]
                .filter(b => b.id == lift(id))
                .updateValue(toBooksRow(id, book, publishers))
                .returning(r => r)
            }
          }
        } yield result
        authors <- updateBookAuthor(book.author, books.id)
      } yield (books, publishers, authors)
    }.map(toBook).provide(dsLayer)
  end update

  /** Обновить автора существующей книги:
    *   - автор не представлен ([[None]]) => происходит удаление записей в
    *     отношении "Книги-Авторы",
    *   - автор представлен ([[Some]]), но его нет в БД => создаётся запись в БД
    *     и возвращается (дополнительно создаётся соответствующая запись в
    *     отношении "Книги-Авторы"),
    *   - автор представлен ([[Some]]) и есть в БД => возвращается запись из БД.
    *
    * @param authorOpt
    *   опциональный автор
    * @param bookId
    *   идентификатор книги
    */
  private def updateBookAuthor(
      authorOpt: Option[Author],
      bookId: UUID
  ): Task[Option[Authors]] =
    authorOpt match
      case None =>
        {
          run {
            quote {
              query[BooksAuthors]
                .filter(ba => ba.bookId == lift(bookId))
                .delete
            }
          }.provide(dsLayer)
        } *> ZIO.none
      case Some(author) if author.id.isEmpty =>
        {
          for {
            id <- Random.nextUUID
            result <- run {
              quote {
                query[Authors]
                  .insertValue(lift(Authors(id, author.name, author.country)))
                  .returning(r => r)
              }
            }
            _ <- run {
              quote {
                query[BooksAuthors]
                  .insertValue(lift(BooksAuthors(bookId, result.id)))
              }
            }
          } yield result
        }.option.provide(dsLayer)
      case Some(author) =>
        {
          for {
            uuid <- ZIO.getOrFail(author.id)
            result <- run {
              quote {
                query[Authors]
                  .filter(a => a.id == lift(uuid))
              }
            }
          } yield result
        }.map(_.headOption).provide(dsLayer)
  end updateBookAuthor

  /** Удалить книгу.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  override def delete(id: String): Task[Unit] =
    for {
      uuid <- ZIO.fromTry(Try(UUID.fromString(id)))
      result <- transaction {
        for {
          _ <- run {
            quote {
              query[BooksAuthors]
                .filter(ba => ba.bookId == lift(uuid))
                .delete
            }
          }
          _ <- run {
            quote {
              query[Books]
                .filter(b => b.id == lift(uuid))
                .delete
            }
          }
        } yield ()
      }.provide(dsLayer)
    } yield result
  end delete

end PgBookRepository

object PgBookRepository:

  /** Слой репозитория книг. */
  val live: ZLayer[Any, Throwable, PgBookRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgBookRepository(_))

end PgBookRepository
