package libra.repositories.userbook

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

  /** Получить ID всех книг в библиотеке пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def libraryBooks(
      userId: String
  ): Task[List[UUID]]

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
  ): Task[Option[Float]]

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
  ): Task[Unit]

  /** Получить среднее значение рейтинга книги.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def avgRating(
      bookId: String
  ): Task[Option[Float]]

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
  ): Task[Option[Int]]

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
