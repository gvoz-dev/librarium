package libra.repositories.comment

import io.getquill.*
import libra.entities.Comment
import libra.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource

/** Реализация репозитория пользовательских комментариев к книгам для СУБД PostgreSQL.
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
        row.userId,
        row.bookId,
        row.text,
        row.isPrivate,
        Some(row.time),
        row.lastModifiedTime
      )

  /** Получить все комментарии. */
  override def all: Task[List[Comment]] =
    run {
      quote {
        query[Comments]
          .map(toComment)
      }
    }.provide(dsLayer)
  end all

  /** Найти комментарий по ID.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  override def findById(id: UUID): Task[Option[Comment]] =
    run {
      quote {
        query[Comments]
          .filter(c => c.id == lift(id))
          .map(toComment)
      }
    }.map(_.headOption).provide(dsLayer)
  end findById

  /** Найти все комментарии пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  override def findByUser(userId: UUID): Task[List[Comment]] =
    run {
      quote {
        query[Comments]
          .filter(c => c.userId == lift(userId))
          .map(toComment)
      }
    }.provide(dsLayer)
  end findByUser

  /** Найти все комментарии к книге.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def findByBook(bookId: UUID): Task[List[Comment]] =
    run {
      quote {
        query[Comments]
          .filter(c => c.bookId == lift(bookId))
          .map(toComment)
      }
    }.provide(dsLayer)
  end findByBook

  /** Найти все комментарии пользователя к книге.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  override def findByUserAndBook(
      userId: UUID,
      bookId: UUID
  ): Task[List[Comment]] =
    run {
      quote {
        query[Comments]
          .filter(c => c.userId == lift(userId) && c.bookId == lift(bookId))
          .map(toComment)
      }
    }.provide(dsLayer)
  end findByUserAndBook

  /** Добавить комментарий пользователя к книге.
    *
    * @param comment
    *   комментарий
    */
  override def create(
      comment: Comment
  ): Task[Comment] =
    for {
      id     <- Random.nextUUID
      time   <- Clock.localDateTime
      result <-
        run {
          quote {
            query[Comments]
              .insertValue(
                lift(
                  Comments(
                    id,
                    comment.userId,
                    comment.bookId,
                    comment.text,
                    comment.isPrivate,
                    time,
                    None
                  )
                )
              )
              .returning(toComment)
          }
        }.provide(dsLayer)
    } yield result
  end create

  /** Изменить комментарий.
    *
    * @param comment
    *   комментарий
    */
  override def update(
      comment: Comment
  ): Task[Comment] =
    for {
      id               <- ZIO.getOrFail(comment.id)
      lastModifiedTime <- Clock.localDateTime.map(Option(_))
      result           <-
        run {
          quote {
            query[Comments]
              .filter(c => c.id == lift(id))
              .update(
                c => c.text -> lift(comment.text),
                c => c.isPrivate -> lift(comment.isPrivate),
                c => c.lastModifiedTime -> lift(lastModifiedTime)
              )
              .returning(toComment)
          }
        }.provide(dsLayer)
    } yield result
  end update

  /** Удалить комментарий.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  override def delete(
      id: UUID
  ): Task[Unit] =
    run {
      quote {
        query[Comments]
          .filter(c => c.id == lift(id))
          .delete
      }
    }.unit.provide(dsLayer)
  end delete

end PgCommentRepository

object PgCommentRepository:

  /** Слой репозитория комментариев. */
  val live: ZLayer[Any, Throwable, PgCommentRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgCommentRepository(_))

end PgCommentRepository
