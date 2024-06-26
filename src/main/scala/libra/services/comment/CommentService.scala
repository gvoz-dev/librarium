package libra.services.comment

import libra.entities.Comment
import libra.repositories.comment.CommentRepository
import libra.utils.ServiceError.*
import zio.ZIO

import java.util.UUID

/** Сервис пользовательских комментариев к книгам. */
object CommentService:

  /** Получить все комментарии. */
  def all: ZIO[CommentRepository, RepositoryError, List[Comment]] =
    ZIO
      .serviceWithZIO[CommentRepository](_.all)
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Comments not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти комментарий по ID.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def findById(
      id: UUID
  ): ZIO[CommentRepository, RepositoryError, Comment] =
    ZIO
      .serviceWithZIO[CommentRepository](_.findById(id))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None          => ZIO.fail(NotFound(s"Comment not found by ID: $id"))
        case Some(comment) => ZIO.succeed(comment)
      }

  /** Найти все комментарии пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def findByUser(
      userId: UUID
  ): ZIO[CommentRepository, RepositoryError, List[Comment]] =
    ZIO
      .serviceWithZIO[CommentRepository](_.findByUser(userId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Comments not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти все комментарии к книге.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByBook(
      bookId: UUID
  ): ZIO[CommentRepository, RepositoryError, List[Comment]] =
    ZIO
      .serviceWithZIO[CommentRepository](_.findByBook(bookId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Comments not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти все комментарии пользователя к книге.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByUserAndBook(
      userId: UUID,
      bookId: UUID
  ): ZIO[CommentRepository, RepositoryError, List[Comment]] =
    ZIO
      .serviceWithZIO[CommentRepository](_.findByUserAndBook(userId, bookId))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Comments not found"))
        case list => ZIO.succeed(list)
      }

  /** Добавить комментарий пользователя к книге.
    *
    * @param comment
    *   комментарий
    */
  def create(
      comment: Comment
  ): ZIO[CommentRepository, InternalServerError, Comment] =
    for {
      result <- ZIO
        .serviceWithZIO[CommentRepository](_.create(comment))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$comment not created:\n${e.prettyPrint}"))
      _      <- ZIO.logInfo(s"$result created")
    } yield result

  /** Изменить комментарий.
    *
    * @param comment
    *   комментарий
    */
  def update(
      comment: Comment
  ): ZIO[CommentRepository, InternalServerError, Comment] =
    for {
      result <- ZIO
        .serviceWithZIO[CommentRepository](_.update(comment))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$comment not updated:\n${e.prettyPrint}"))
      _      <- ZIO.logInfo(s"$result updated")
    } yield result

  /** Удалить комментарий.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def delete(
      id: UUID
  ): ZIO[CommentRepository, InternalServerError, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[CommentRepository](_.delete(id))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"Comment ($id) not deleted:\n${e.prettyPrint}"))
      _ <- ZIO.logInfo(s"Comment ($id) deleted")
    } yield ()

end CommentService
