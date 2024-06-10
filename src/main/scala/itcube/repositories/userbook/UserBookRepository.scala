package itcube.repositories.userbook

import zio.*

import java.util.UUID

/** Репозиторий отношений "Пользователь-Книга". */
trait UserBookRepository:

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
  ): Task[Option[UUID]]

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
  ): Task[Unit]

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
  ): Task[Unit]

  /** Установить прогресс - % читаемой пользователем книги.
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
  ): Task[Unit]

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
  ): Task[Unit]

end UserBookRepository

object UserBookRepository:

  /** Сервис получения ID отношения "Пользователь-Книга".
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

  /** Сервис добавления книги в библиотеку пользователя.
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
    ZIO.serviceWithZIO[UserBookRepository](_.addToLibrary(userId, bookId))

  /** Сервис удаления книги из библиотеки пользователя.
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
    ZIO.serviceWithZIO[UserBookRepository](_.deleteFromLibrary(userId, bookId))

  /** Сервис установки прогресса - % читаемой пользователем книги.
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
    ZIO.serviceWithZIO[UserBookRepository](
      _.setProgress(progress, userId, bookId)
    )

  /** Сервис установки пользовательского рейтинга книги.
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
    ZIO.serviceWithZIO[UserBookRepository](_.setRating(rating, userId, bookId))

end UserBookRepository
