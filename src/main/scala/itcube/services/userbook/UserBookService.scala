package itcube.services.userbook

import itcube.repositories.userbook.UserBookRepository
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
      userId: String,
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Option[UUID]] =
    ZIO.serviceWithZIO[UserBookRepository](_.findUserBook(userId, bookId))

  /** Получить ID всех книг в библиотеке пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def libraryBooks(
      userId: String
  ): ZIO[UserBookRepository, Throwable, List[UUID]] =
    ZIO.serviceWithZIO[UserBookRepository](_.libraryBooks(userId))

  /** Добавить книгу в библиотеку пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def addToLibrary(
      userId: String,
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserBookRepository](_.addToLibrary(userId, bookId))
        .onError(e =>
          ZIO.logError(
            s"Book `$bookId` not added to lib `$userId`:\n${e.prettyPrint}"
          )
        )
      _ <- ZIO.logInfo(s"Book `$bookId` added to lib `$userId`")
    } yield result

  /** Удалить книгу из библиотеки пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def deleteFromLibrary(
      userId: String,
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[UserBookRepository](_.deleteFromLibrary(userId, bookId))
        .onError(e =>
          ZIO.logError(
            s"Book `$bookId` not deleted from lib `$userId`:\n${e.prettyPrint}"
          )
        )
      _ <- ZIO.logInfo(s"Book `$bookId` deleted from lib `$userId`")
    } yield ()

  /** Получить прогресс прочитанного.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def getProgress(
      userId: String,
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Option[Float]] =
    ZIO.serviceWithZIO[UserBookRepository](_.getProgress(userId, bookId))

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
      progress: Float,
      userId: String,
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserBookRepository](
          _.setProgress(progress, userId, bookId)
        )
        .onError(e =>
          ZIO.logError(
            s"Progress not set (`$userId`, `$bookId`):\n${e.prettyPrint}"
          )
        )
      _ <- ZIO.logInfo(s"Progress $progress set (`$userId`, `$bookId`)")
    } yield result

  /** Получить среднее значение рейтинга книги.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def avgRating(
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Option[Float]] =
    ZIO.serviceWithZIO[UserBookRepository](_.avgRating(bookId))

  /** Получить пользовательский рейтинг книги.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def getRating(
      userId: String,
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Option[Int]] =
    ZIO.serviceWithZIO[UserBookRepository](_.getRating(userId, bookId))

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
      rating: Int,
      userId: String,
      bookId: String
  ): ZIO[UserBookRepository, Throwable, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserBookRepository](
          _.setRating(rating, userId, bookId)
        )
        .onError(e =>
          ZIO.logError(
            s"Rating not set (`$userId`, `$bookId`):\n${e.prettyPrint}"
          )
        )
      _ <- ZIO.logInfo(s"Rating $rating set (`$userId`, `$bookId`)")
    } yield result

end UserBookService
