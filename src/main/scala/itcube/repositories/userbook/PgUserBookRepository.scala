package itcube.repositories.userbook

import io.getquill.*
import itcube.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource
import scala.util.Try

/** Реализация репозитория отношений "Пользователь-Книга" для СУБД PostgreSQL.
  *
  * @param ds
  *   источник данных [[DataSource]]
  */
case class PgUserBookRepository(ds: DataSource) extends UserBookRepository:

  import PostgresContext.*

  private val dsLayer: ULayer[DataSource] = ZLayer.succeed(ds)

  /** Получить ID отношения "Пользователь-Книга".
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def findUserBook(
      userId: String,
      bookId: String
  ): Task[Option[UUID]] =
    for {
      userUuid <- ZIO.fromTry(Try(UUID.fromString(userId)))
      bookUuid <- ZIO.fromTry(Try(UUID.fromString(bookId)))
      userBookUuid <- run {
        quote {
          query[UsersBooks]
            .filter(ub =>
              ub.userId == lift(userUuid) && ub.bookId == lift(bookUuid)
            )
            .map(ub => ub.id)
        }
      }.map(_.headOption)
        .provide(dsLayer)
    } yield userBookUuid
  end findUserBook

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
  private def createUserBook(
      userId: String,
      bookId: String,
      inLib: Boolean,
      progress: Float,
      rating: Int
  ): Task[UUID] =
    for {
      userUuid <- ZIO.fromTry(Try(UUID.fromString(userId)))
      bookUuid <- ZIO.fromTry(Try(UUID.fromString(bookId)))
      uuid <- Random.nextUUID
      userBookUuid <- run {
        quote {
          query[UsersBooks]
            .insertValue(
              lift(
                UsersBooks(uuid, userUuid, bookUuid, inLib, progress, rating)
              )
            )
            .returning(_.id)
        }
      }.provide(dsLayer)
    } yield userBookUuid
  end createUserBook

  /** Добавить книгу в библиотеку пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def addToLibrary(
      userId: String,
      bookId: String
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setInLibStatus(true, uuid)
        case None       => createUserBook(userId, bookId, true, 0, 0).unit
      }
    }.provide(dsLayer)
  end addToLibrary

  /** Установить значение свойства inLibrary в отношении "Пользователь-Книга".
    *
    * @param inLib
    *   находится или нет книга в библиотеке пользователя
    * @param userBookUuid
    *   уникальный идентификатор отношения "Пользователь-Книга"
    */
  private def setInLibStatus(
      inLib: Boolean,
      userBookUuid: UUID
  ): Task[Unit] =
    run {
      quote {
        query[UsersBooks]
          .filter(_.id == lift(userBookUuid))
          .update(_.inLibrary -> lift(inLib))
      }
    }.unit
      .provide(dsLayer)
  end setInLibStatus

  /** Удалить книгу из библиотеки пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def deleteFromLibrary(
      userId: String,
      bookId: String
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setInLibStatus(false, uuid)
        case None       => ZIO.unit
      }
    }.provide(dsLayer)
  end deleteFromLibrary

  /** Установить прогресс - % читаемой пользователем книги.
    *
    * @param progress
    *   прогресс от 0.0 до 100.0 (%)
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def setProgress(
      progress: Float,
      userId: String,
      bookId: String
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setProgressStatus(progress, uuid)
        case None => createUserBook(userId, bookId, false, progress, 0).unit
      }
    }.provide(dsLayer)
  end setProgress

  /** Установить прогресс чтения книги в отношении "Пользователь-Книга".
    *
    * @param progress
    *   прогресс от 0.0 до 100.0 (%)
    * @param userBookUuid
    *   уникальный идентификатор отношения "Пользователь-Книга"
    */
  private def setProgressStatus(
      progress: Float,
      userBookUuid: UUID
  ): Task[Unit] =
    run {
      quote {
        query[UsersBooks]
          .filter(_.id == lift(userBookUuid))
          .update(_.progress -> lift(progress))
      }
    }.unit
      .provide(dsLayer)
  end setProgressStatus

  /** Установить пользовательский рейтинг книги.
    *
    * @param rating
    *   ретинг от 0 до 5
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def setRating(
      rating: Int,
      userId: String,
      bookId: String
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setRatingStatus(rating, uuid)
        case None       => createUserBook(userId, bookId, false, 0, rating).unit
      }
    }.provide(dsLayer)
  end setRating

  /** Установить рейтинг книги в отношении "Пользователь-Книга".
    *
    * @param rating
    *   ретинг от 0 до 5
    * @param userBookUuid
    *   уникальный идентификатор отношения "Пользователь-Книга"
    */
  private def setRatingStatus(
      rating: Int,
      userBookUuid: UUID
  ): Task[Unit] =
    run {
      quote {
        query[UsersBooks]
          .filter(_.id == lift(userBookUuid))
          .update(_.rating -> lift(rating))
      }
    }.unit
      .provide(dsLayer)
  end setRatingStatus

end PgUserBookRepository

object PgUserBookRepository:

  /** Слой репозитория отношений "Пользователь-Книга". */
  val live: ZLayer[Any, Throwable, PgUserBookRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(ds =>
      PgUserBookRepository(ds)
    )

end PgUserBookRepository
