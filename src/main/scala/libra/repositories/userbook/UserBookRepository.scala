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
      userId: UUID,
      bookId: UUID
  ): Task[Option[UUID]]

  /** Создать запись отношения "Пользователь-Книга".
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    * @param inLib
    *   находится или нет книга в библиотеке пользователя
    * @param progress
    *   прогресс от 0.0 до 100.0 (%)
    * @param rating
    *   ретинг от 0 до 5
    */
  def createUserBook(
      userId: UUID,
      bookId: UUID,
      inLib: Boolean,
      progress: Float,
      rating: Int
  ): Task[UUID]

  /** Получить ID всех книг в библиотеке пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def libraryBooks(
      userId: UUID
  ): Task[List[UUID]]

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
  ): Task[Unit]

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
  ): Task[Unit]

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
      userId: UUID,
      bookId: UUID,
      progress: Float
  ): Task[Unit]

  /** Получить среднее значение рейтинга книги.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def avgRating(
      bookId: UUID
  ): Task[Option[Float]]

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
      userId: UUID,
      bookId: UUID,
      rating: Int
  ): Task[Unit]

end UserBookRepository
