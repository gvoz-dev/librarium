package itcube.repositories.comment

import io.getquill.*
import itcube.entities.Comment
import itcube.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource
import scala.util.Try

/** Реализация репозитория пользовательских комментариев к книгам для СУБД
  * PostgreSQL.
  *
  * @param ds
  *   источник данных [[DataSource]]
  */
case class PgCommentRepository(ds: DataSource) extends CommentRepository:

  import PostgresContext.*

  private val dsLayer: ULayer[DataSource] = ZLayer.succeed(ds)

  /** Преобразование строки таблицы в сущность "Комментарий". */
  private inline def toComment: Comments => Comment =
    row =>
      Comment(
        Some(row.id),
        row.text,
        row.isPrivate,
        Some(row.date)
      )

  /** Добавить комментарий пользователя к книге.
    *
    * @param comment
    *   комментарий
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def create(
      comment: Comment,
      userId: String,
      bookId: String
  ): Task[Option[Comment]] =
    transaction {
      for {
        userUuid <- ZIO.fromTry(Try(UUID.fromString(userId)))
        bookUuid <- ZIO.fromTry(Try(UUID.fromString(bookId)))
        userBookUuid <- findUserBook(userUuid, bookUuid) flatMap {
          case Some(uuid) => ZIO.succeed(uuid)
          case None       => createUserBook(userUuid, bookUuid)
        }
        comment <- createComment(comment, userBookUuid)
      } yield comment
    }.option
      .provide(dsLayer)
  end create

  /** Получить запись отношения "Пользователь-Книга". Если запись в таблице
    * существует, вернуть её ID.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  private def findUserBook(userId: UUID, bookId: UUID): Task[Option[UUID]] =
    run {
      quote {
        query[UsersBooks]
          .filter(ub => ub.userId == lift(userId) && ub.bookId == lift(bookId))
          .map(ub => ub.id)
      }
    }.map(_.headOption)
      .provide(dsLayer)
  end findUserBook

  /** Создать запись отношения "Пользователь-Книга" и вернуть её ID. При
    * создании отношения свойство inLibrary = false. Т.е. книга не добавляется в
    * библиотеку пользователя при комментировании.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  private def createUserBook(userId: UUID, bookId: UUID): Task[UUID] =
    for {
      uuid <- Random.nextUUID
      userBookUuid <- run {
        quote {
          query[UsersBooks]
            .insertValue(lift(UsersBooks(uuid, userId, bookId, false, 0, 0)))
            .returning(ub => ub.id)
        }
      }.provide(dsLayer)
    } yield userBookUuid
  end createUserBook

  /** Сохранить комментарий в БД и вернуть его.
    *
    * @param comment
    *   комментарий
    * @param userBookUuid
    *   уникальный идентификатор отношения "Пользователь-Книга"
    */
  private def createComment(
      comment: Comment,
      userBookUuid: UUID
  ): Task[Comment] =
    for {
      uuid <- Random.nextUUID
      date <- Clock.localDateTime
      result <- run {
        quote {
          query[Comments]
            .insertValue(
              lift(
                Comments(
                  uuid,
                  userBookUuid,
                  comment.text,
                  comment.isPrivate,
                  date
                )
              )
            )
            .returning(toComment)
        }
      }.provide(dsLayer)
    } yield result
  end createComment

  /** Получить все комментарии пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  override def findByUser(userId: String): Task[List[Comment]] =
    for {
      userUuid <- ZIO.fromTry(Try(UUID.fromString(userId)))
      result <- run {
        quote {
          for {
            usersBooks <- query[UsersBooks].filter(_.userId == lift(userUuid))
            comments <- query[Comments].join(_.userBookId == usersBooks.id)
          } yield comments
        }.map(toComment)
      }.provide(dsLayer)
    } yield result
  end findByUser

  /** Получить все комментарии к книге.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByBook(bookId: String): Task[List[Comment]] =
    for {
      bookUuid <- ZIO.fromTry(Try(UUID.fromString(bookId)))
      result <- run {
        quote {
          for {
            usersBooks <- query[UsersBooks].filter(_.bookId == lift(bookUuid))
            comments <- query[Comments].join(_.userBookId == usersBooks.id)
          } yield comments
        }.map(toComment)
      }.provide(dsLayer)
    } yield result
  end findByBook

  /** Получить все комментарии пользователя к книге.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByUserAndBook(userId: String, bookId: String): Task[List[Comment]] =
    for {
      userUuid <- ZIO.fromTry(Try(UUID.fromString(userId)))
      bookUuid <- ZIO.fromTry(Try(UUID.fromString(bookId)))
      result <- run {
        quote {
          for {
            usersBooks <- query[UsersBooks]
              .filter(ub =>
                ub.userId == lift(userUuid) && ub.bookId == lift(bookUuid)
              )
            comments <- query[Comments].join(_.userBookId == usersBooks.id)
          } yield comments
        }.map(toComment)
      }.provide(dsLayer)
    } yield result
  end findByUserAndBook

  /** Изменить комментарий.
    *
    * @param comment
    *   комментарий
    */
  def update(comment: Comment): Task[Option[Comment]] =
    for {
      id <- ZIO.getOrFail(comment.id)
      result <- run {
        quote {
          query[Comments]
            .filter(_.id == lift(id))
            .update(
              _.text -> lift(comment.text),
              _.isPrivate -> lift(comment.isPrivate)
            )
            .returning(toComment)
        }
      }.option
        .provide(dsLayer)
    } yield result
  end update

  /** Удалить комментарий.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def delete(id: String): Task[Unit] =
    for {
      uuid <- ZIO.fromTry(Try(UUID.fromString(id)))
      _ <- run {
        quote {
          query[Comments]
            .filter(_.id == lift(uuid))
            .delete
        }
      }.provide(dsLayer)
    } yield ()
  end delete

end PgCommentRepository

object PgCommentRepository:

  /** Слой репозитория комментариев. */
  val live: ZLayer[Any, Throwable, PgCommentRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(ds =>
      PgCommentRepository(ds)
    )

end PgCommentRepository
