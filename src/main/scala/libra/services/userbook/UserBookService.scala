package libra.services.userbook

import libra.repositories.userbook.UserBookRepository
import libra.utils.ServiceError.*
import zio.ZIO

import java.util.UUID

/** Сервис отношений "Пользователь-Книга". */
object UserBookService:

  /** Получить ID отношения "Пользователь-Книга".
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findUserBook(
      userId: UUID,
      bookId: UUID
  ): ZIO[UserBookRepository, RepositoryError, UUID] =
    ZIO
      .serviceWithZIO[UserBookRepository](_.findUserBook(userId, bookId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None       => ZIO.fail(NotFound("UserBook ID not found"))
        case Some(uuid) => ZIO.succeed(uuid)
      }

  /** Получить ID всех книг в библиотеке пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def libraryBooks(
      userId: UUID
  ): ZIO[UserBookRepository, RepositoryError, List[UUID]] =
    ZIO
      .serviceWithZIO[UserBookRepository](_.libraryBooks(userId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Library books not found"))
        case list => ZIO.succeed(list)
      }

  /** Добавить книгу в библиотеку пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def addToLibrary(
      userId: UUID,
      bookId: UUID
  ): ZIO[UserBookRepository, InternalServerError, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserBookRepository](_.addToLibrary(userId, bookId))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"Book ($bookId) not added to lib ($userId):\n${e.prettyPrint}"))
      _      <- ZIO.logInfo(s"Book ($bookId) added to lib ($userId)")
    } yield result

  /** Удалить книгу из библиотеки пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def deleteFromLibrary(
      userId: UUID,
      bookId: UUID
  ): ZIO[UserBookRepository, InternalServerError, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[UserBookRepository](_.deleteFromLibrary(userId, bookId))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"Book ($bookId) not deleted from lib ($userId):\n${e.prettyPrint}"))
      _ <- ZIO.logInfo(s"Book ($bookId) deleted from lib ($userId)")
    } yield ()

  /** Получить прогресс прочитанного.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def getProgress(
      userId: UUID,
      bookId: UUID
  ): ZIO[UserBookRepository, RepositoryError, Float] =
    ZIO
      .serviceWithZIO[UserBookRepository](_.getProgress(userId, bookId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None           => ZIO.fail(NotFound(s"Progress not found"))
        case Some(progress) => ZIO.succeed(progress)
      }

  /** Установить прогресс прочитанного.
    *
    * @param progress
    *   прогресс от 0.0 до 100.0 (%)
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def setProgress(
      userId: UUID,
      bookId: UUID,
      progress: Float
  ): ZIO[UserBookRepository, InternalServerError, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserBookRepository](_.setProgress(userId, bookId, progress))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"Progress not set:\n${e.prettyPrint}"))
      _      <- ZIO.logInfo(s"Progress $progress set ($userId, $bookId)")
    } yield result

  /** Получить среднее значение рейтинга книги.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def avgRating(
      bookId: UUID
  ): ZIO[UserBookRepository, RepositoryError, Float] =
    ZIO
      .serviceWithZIO[UserBookRepository](_.avgRating(bookId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None         => ZIO.fail(NotFound(s"Rating not found"))
        case Some(rating) => ZIO.succeed(rating)
      }

  /** Получить пользовательский рейтинг книги.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def getRating(
      userId: UUID,
      bookId: UUID
  ): ZIO[UserBookRepository, RepositoryError, Int] =
    ZIO
      .serviceWithZIO[UserBookRepository](_.getRating(userId, bookId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None         => ZIO.fail(NotFound(s"Rating not found"))
        case Some(rating) => ZIO.succeed(rating)
      }

  /** Установить пользовательский рейтинг книги.
    *
    * @param rating
    *   ретинг от 0 до 5
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def setRating(
      userId: UUID,
      bookId: UUID,
      rating: Int
  ): ZIO[UserBookRepository, InternalServerError, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserBookRepository](_.setRating(userId, bookId, rating))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"Rating not set:\n${e.prettyPrint}"))
      _      <- ZIO.logInfo(s"Rating $rating set ($userId, $bookId)")
    } yield result

end UserBookService
