package itcube.services.comment

import itcube.entities.Comment
import itcube.repositories.comment.CommentRepository
import zio.ZIO

/** Сервис пользовательских комментариев к книгам. */
object CommentService:

  /** Получить комментарий по ID.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def findById(
      id: String
  ): ZIO[CommentRepository, Throwable, Option[Comment]] =
    ZIO.serviceWithZIO[CommentRepository](_.findById(id))

  /** Сервис получения всех комментариев пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def findByUser(
      userId: String
  ): ZIO[CommentRepository, Throwable, List[Comment]] =
    ZIO.serviceWithZIO[CommentRepository](_.findByUser(userId))

  /** Сервис получения всех комментариев к книге.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByBook(
      bookId: String
  ): ZIO[CommentRepository, Throwable, List[Comment]] =
    ZIO.serviceWithZIO[CommentRepository](_.findByBook(bookId))

  /** Сервис получения всех комментариев пользователя к книге.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByUserAndBook(
      userId: String,
      bookId: String
  ): ZIO[CommentRepository, Throwable, List[Comment]] =
    ZIO.serviceWithZIO[CommentRepository](_.findByUserAndBook(userId, bookId))

  /** Сервис добавления комментария пользователя к книге.
    *
    * @param comment
    *   комментарий
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def create(
      comment: Comment,
      userId: String,
      bookId: String
  ): ZIO[CommentRepository, Throwable, Comment] =
    for {
      result <- ZIO
        .serviceWithZIO[CommentRepository](_.create(comment, userId, bookId))
        .onError(e =>
          ZIO.logError(s"Comment `$comment` not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Comment `$result` created")
    } yield result

  /** Сервис изменения комментария.
    *
    * @param comment
    *   комментарий
    */
  def update(
      comment: Comment
  ): ZIO[CommentRepository, Throwable, Comment] =
    for {
      result <- ZIO
        .serviceWithZIO[CommentRepository](_.update(comment))
        .onError(e =>
          ZIO.logError(s"Comment `$comment` not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Comment `$result` updated")
    } yield result

  /** Сервис удаления комментария.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def delete(id: String): ZIO[CommentRepository, Throwable, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[CommentRepository](_.delete(id))
        .onError(e =>
          ZIO.logError(s"Comment `$id` not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Comment `$id` deleted")
    } yield ()

end CommentService
