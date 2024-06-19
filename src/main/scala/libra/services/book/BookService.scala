package libra.services.book

import libra.entities.Book
import libra.repositories.book.BookRepository
import zio.ZIO

/** Сервис книг. */
object BookService:

  /** Получить все книги. */
  def all: ZIO[BookRepository, Throwable, List[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.all)

  /** Получить книгу по ID.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[BookRepository, Throwable, Option[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.findById(id))

  /** Получить книгу по названию.
    *
    * @param title
    *   название книги
    */
  def findByTitle(
      title: String
  ): ZIO[BookRepository, Throwable, Option[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.findByTitle(title))

  /** Создать книгу.
    *
    * @param book
    *   книга
    */
  def create(
      book: Book
  ): ZIO[BookRepository, Throwable, Book] =
    for {
      result <- ZIO
        .serviceWithZIO[BookRepository](_.create(book))
        .onError(e =>
          ZIO.logError(s"Book `$book` not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Book `$result` created")
    } yield result

  /** Изменить книгу.
    *
    * @param book
    *   книга
    */
  def update(
      book: Book
  ): ZIO[BookRepository, Throwable, Book] =
    for {
      result <- ZIO
        .serviceWithZIO[BookRepository](_.update(book))
        .onError(e =>
          ZIO.logError(s"Book `$book` not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Book `$result` updated")
    } yield result

  /** Удалить книгу.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[BookRepository, Throwable, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[BookRepository](_.delete(id))
        .onError(e =>
          ZIO.logError(s"Book `$id` not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Book `$id` deleted")
    } yield ()

end BookService
