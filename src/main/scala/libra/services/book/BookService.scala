package libra.services.book

import libra.entities.Book
import libra.repositories.book.BookRepository
import libra.utils.ServiceError.*
import zio.ZIO

/** Сервис книг. */
object BookService:

  /** Получить все книги. */
  def all: ZIO[BookRepository, RepositoryError, List[Book]] =
    ZIO
      .serviceWithZIO[BookRepository](_.all)
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Books not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти книгу по ID.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[BookRepository, RepositoryError, Book] =
    ZIO
      .serviceWithZIO[BookRepository](_.findById(id))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None         => ZIO.fail(NotFound(s"Book not found by ID: $id"))
        case Some(author) => ZIO.succeed(author)
      }

  /** Найти книгу по названию.
    *
    * @param title
    *   название книги
    */
  def findByTitle(
      title: String
  ): ZIO[BookRepository, RepositoryError, List[Book]] =
    ZIO
      .serviceWithZIO[BookRepository](_.findByTitle(title))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound(s"Books not found by name: $title"))
        case list => ZIO.succeed(list)
      }

  /** Создать книгу.
    *
    * @param book
    *   книга
    */
  def create(
      book: Book
  ): ZIO[BookRepository, InternalServerError, Book] =
    for {
      result <- ZIO
        .serviceWithZIO[BookRepository](_.create(book))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$book not created:\n${e.prettyPrint}"))
      _ <- ZIO.logInfo(s"$result created")
    } yield result

  /** Изменить книгу.
    *
    * @param book
    *   книга
    */
  def update(
      book: Book
  ): ZIO[BookRepository, InternalServerError, Book] =
    for {
      result <- ZIO
        .serviceWithZIO[BookRepository](_.update(book))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$book not updated:\n${e.prettyPrint}"))
      _ <- ZIO.logInfo(s"$result updated")
    } yield result

  /** Удалить книгу.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[BookRepository, InternalServerError, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[BookRepository](_.delete(id))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"Book ($id) not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Book ($id) deleted")
    } yield ()

end BookService
