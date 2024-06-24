package libra.repositories.userbook

import io.getquill.*
import libra.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource

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
      userId: UUID,
      bookId: UUID
  ): Task[Option[UUID]] =
    for {
      result <- run {
        quote {
          query[UsersBooks]
            .filter(ub =>
              ub.userId == lift(userId) && ub.bookId == lift(bookId)
            )
            .map(ub => ub.id)
        }
      }.map(_.headOption).provide(dsLayer)
    } yield result
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
      userId: UUID,
      bookId: UUID,
      inLib: Boolean,
      progress: Float,
      rating: Int
  ): Task[UUID] =
    transaction {
      for {
        // Fail, если заданные пользователь и книга не существуют
        _ <- run { quote { query[Users].filter(u => u.id == lift(userId)) } }
          .map(_.headOption)
          .someOrFail(Exception(s"User ($userId) not found"))
        _ <- run { quote { query[Books].filter(b => b.id == lift(bookId)) } }
          .map(_.headOption)
          .someOrFail(Exception(s"Book ($bookId) not found"))
        uuid <- Random.nextUUID
        result <- run {
          quote {
            query[UsersBooks]
              .insertValue(
                lift(
                  UsersBooks(uuid, userId, bookId, inLib, progress, rating)
                )
              )
              .returning(ub => ub.id)
          }
        }
      } yield result
    }.provide(dsLayer)
  end createUserBook

  /** Получить ID всех книг в библиотеке пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  override def libraryBooks(
      userId: UUID
  ): Task[List[UUID]] =
    for {
      result <- run {
        quote {
          query[UsersBooks]
            .filter(ub => ub.userId == lift(userId) && ub.inLibrary)
            .map(ub => ub.bookId)
        }
      }.provide(dsLayer)
    } yield result

  /** Добавить книгу в библиотеку пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def addToLibrary(
      userId: UUID,
      bookId: UUID
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setInLibraryStatus(uuid, true)
        case None       => createUserBook(userId, bookId, true, 0, 0).unit
      }
    }.provide(dsLayer)
  end addToLibrary

  /** Удалить книгу из библиотеки пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def deleteFromLibrary(
      userId: UUID,
      bookId: UUID
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setInLibraryStatus(uuid, false)
        case None       => ZIO.unit
      }
    }.provide(dsLayer)
  end deleteFromLibrary

  /** Установить значение свойства inLibrary в отношении "Пользователь-Книга".
    *
    * @param userBookUuid
    *   уникальный идентификатор отношения "Пользователь-Книга"
    * @param inLib
    *   находится или нет книга в библиотеке пользователя
    */
  private def setInLibraryStatus(
      userBookUuid: UUID,
      inLib: Boolean
  ): Task[Unit] =
    run {
      quote {
        query[UsersBooks]
          .filter(ub => ub.id == lift(userBookUuid))
          .update(ub => ub.inLibrary -> lift(inLib))
      }
    }.unit.provide(dsLayer)
  end setInLibraryStatus

  /** Получить прогресс прочитанного.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def getProgress(
      userId: UUID,
      bookId: UUID
  ): Task[Option[Float]] =
    for {
      result <- run {
        quote {
          query[UsersBooks]
            .filter(ub =>
              ub.userId == lift(userId) && ub.bookId == lift(bookId)
            )
            .map(ub => ub.progress)
        }
      }.map(_.headOption).provide(dsLayer)
    } yield result

  /** Установить прогресс прочитанного.
    *
    * @param progress
    *   прогресс от 0.0 до 100.0 (%)
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def setProgress(
      userId: UUID,
      bookId: UUID,
      progress: Float
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setProgressStatus(uuid, progress)
        case None => createUserBook(userId, bookId, false, progress, 0).unit
      }
    }.provide(dsLayer)
  end setProgress

  /** Установить прогресс прочитанного в отношении "Пользователь-Книга".
    *
    * @param userBookUuid
    *   уникальный идентификатор отношения "Пользователь-Книга"
    * @param progress
    *   прогресс от 0.0 до 100.0 (%)
    */
  private def setProgressStatus(
      userBookUuid: UUID,
      progress: Float
  ): Task[Unit] =
    run {
      quote {
        query[UsersBooks]
          .filter(ub => ub.id == lift(userBookUuid))
          .update(ub => ub.progress -> lift(progress))
      }
    }.unit.provide(dsLayer)
  end setProgressStatus

  /** Получить среднее значение рейтинга книги.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def avgRating(
      bookId: UUID
  ): Task[Option[Float]] =
    for {
      result <- run {
        quote {
          query[UsersBooks]
            .filter(ub => ub.bookId == lift(bookId))
            .map(ub => ub.rating)
            .avg
        }
      }.provide(dsLayer)
    } yield result.map(_.toFloat)
  end avgRating

  /** Получить пользовательский рейтинг книги.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def getRating(
      userId: UUID,
      bookId: UUID
  ): Task[Option[Int]] =
    for {
      result <- run {
        quote {
          query[UsersBooks]
            .filter(ub =>
              ub.userId == lift(userId) && ub.bookId == lift(bookId)
            )
            .map(ub => ub.rating)
        }
      }.map(_.headOption).provide(dsLayer)
    } yield result

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
      userId: UUID,
      bookId: UUID,
      rating: Int
  ): Task[Unit] =
    transaction {
      findUserBook(userId, bookId) flatMap {
        case Some(uuid) => setRatingStatus(uuid, rating)
        case None       => createUserBook(userId, bookId, false, 0, rating).unit
      }
    }.provide(dsLayer)
  end setRating

  /** Установить рейтинг книги в отношении "Пользователь-Книга".
    *
    * @param userBookUuid
    *   уникальный идентификатор отношения "Пользователь-Книга"
    * @param rating
    *   ретинг от 0 до 5
    */
  private def setRatingStatus(
      userBookUuid: UUID,
      rating: Int
  ): Task[Unit] =
    run {
      quote {
        query[UsersBooks]
          .filter(ub => ub.id == lift(userBookUuid))
          .update(ub => ub.rating -> lift(rating))
      }
    }.unit.provide(dsLayer)
  end setRatingStatus

end PgUserBookRepository

object PgUserBookRepository:

  /** Слой репозитория отношений "Пользователь-Книга". */
  val live: ZLayer[Any, Throwable, PgUserBookRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgUserBookRepository(_))

end PgUserBookRepository
