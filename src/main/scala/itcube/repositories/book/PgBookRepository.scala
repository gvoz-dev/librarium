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
          publishers <- query[Publishers].leftJoin(p =>
            books.publisherId.contains(p.id)
          )
          booksAuthors <- query[BooksAuthors].leftJoin(_.bookId == books.id)
          authors <- query[Authors].leftJoin(a =>
            booksAuthors.exists(_.authorId == a.id)
          )
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
            books <- query[Books].filter(_.id == lift(uuid))
            publishers <- query[Publishers].leftJoin(p =>
              books.publisherId.contains(p.id)
            )
            booksAuthors <- query[BooksAuthors].leftJoin(ba =>
              books.id == ba.bookId
            )
            authors <- query[Authors].leftJoin(a =>
              booksAuthors.exists(_.authorId == a.id)
            )
          } yield (books, publishers, authors)
        }.map(toBook)
      }.map(_.headOption)
        .provide(dsLayer)
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
          books <- query[Books].filter(_.title == lift(title))
          publishers <- query[Publishers].leftJoin(p =>
            books.publisherId.contains(p.id)
          )
          booksAuthors <- query[BooksAuthors].leftJoin(ba =>
            books.id == ba.bookId
          )
          authors <- query[Authors].leftJoin(a =>
            booksAuthors.exists(_.authorId == a.id)
          )
        } yield (books, publishers, authors)
      }.map(toBook)
    }.map(_.headOption)
      .provide(dsLayer)
  end findByTitle

  /** Создать книгу.
    *
    * @param book
    *   книга
    */
  override def create(book: Book): Task[Option[Book]] =
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
    }.map(toBook)
      .option
      .provide(dsLayer)
  end create

  /** Создать издателя книги, если он представлен, но его нет в БД.
    *
    * @param publisherOpt
    *   издатель, если представлен
    */
  private def createBookPublisher(
      publisherOpt: Option[Publisher]
  ): Task[Option[Publishers]] =
    if publisherOpt.isEmpty then ZIO.none
    else
      val publisher = publisherOpt.get
      if publisher.id.isEmpty then
        for {
          id <- Random.nextUUID
          publishers <- run {
            quote {
              query[Publishers]
                .insertValue(
                  lift(Publishers(id, publisher.name, publisher.country))
                )
                .returning(r => r)
            }
          }.option
            .provide(dsLayer)
        } yield publishers
      else
        for {
          uuid <- ZIO.getOrFail(publisher.id)
          publishers <- run {
            quote {
              query[Publishers]
                .filter(_.id == lift(uuid))
            }
          }.map(_.headOption)
            .provide(dsLayer)
        } yield publishers
  end createBookPublisher

  /** Создать автора книги, если он представлен, но его нет в БД.
    *
    * @param authorOpt
    *   автор, если представлен
    * @param bookId
    *   идентификатор книги
    */
  private def createBookAuthor(
      authorOpt: Option[Author],
      bookId: UUID
  ): Task[Option[Authors]] =
    if authorOpt.isEmpty then ZIO.none
    else
      val author = authorOpt.get
      if author.id.isEmpty then
        val created = for {
          id <- Random.nextUUID
          authors <- run {
            quote {
              query[Authors]
                .insertValue(lift(Authors(id, author.name, author.country)))
                .returning(r => r)
            }
          }
          _ <- run {
            quote {
              query[BooksAuthors]
                .insertValue(lift(BooksAuthors(bookId, authors.id)))
            }
          }
        } yield authors
        created.option.provide(dsLayer)
      else
        val selected = for {
          uuid <- ZIO.getOrFail(author.id)
          authors <- run {
            quote {
              query[Authors]
                .filter(_.id == lift(uuid))
            }
          }
          _ <- run {
            quote {
              query[BooksAuthors]
                .insertValue(lift(BooksAuthors(bookId, uuid)))
            }
          }
        } yield authors
        selected.map(_.headOption).provide(dsLayer)
  end createBookAuthor

  /** Изменить книгу.
    *
    * @param book
    *   книга
    */
  override def update(book: Book): Task[Option[Book]] =
    transaction {
      for {
        publishers <- createBookPublisher(book.publisher)
        books <- for {
          id <- ZIO.getOrFail(book.id)
          result <- run {
            quote {
              query[Books]
                .filter(_.id == lift(id))
                .updateValue(toBooksRow(id, book, publishers))
                .returning(r => r)
            }
          }
        } yield result
        authors <- updateBookAuthor(book.author, books.id)
      } yield (books, publishers, authors)
    }.map(toBook)
      .option
      .provide(dsLayer)
  end update

  /** Обновить автора книги.
    *
    * @param authorOpt
    *   автор, если представлен
    * @param bookId
    *   идентификатор книги
    */
  private def updateBookAuthor(
      authorOpt: Option[Author],
      bookId: UUID
  ): Task[Option[Authors]] =
    if authorOpt.isEmpty then ZIO.none
    else
      val author = authorOpt.get
      if author.id.isEmpty then
        val created = for {
          id <- Random.nextUUID
          authors <- run {
            quote {
              query[Authors]
                .insertValue(lift(Authors(id, author.name, author.country)))
                .returning(r => r)
            }
          }
          _ <- run {
            quote {
              query[BooksAuthors]
                .insertValue(lift(BooksAuthors(bookId, authors.id)))
            }
          }
        } yield authors
        created.option.provide(dsLayer)
      else
        val selected = for {
          uuid <- ZIO.getOrFail(author.id)
          authors <- run {
            quote {
              query[Authors]
                .filter(_.id == lift(uuid))
            }
          }
        } yield authors
        selected.map(_.headOption).provide(dsLayer)
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
                .filter(_.bookId == lift(uuid))
                .delete
            }
          }
          _ <- run {
            quote {
              query[Books]
                .filter(_.id == lift(uuid))
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
    PostgresDataSource.live >>> ZLayer.fromFunction(ds => PgBookRepository(ds))

end PgBookRepository
