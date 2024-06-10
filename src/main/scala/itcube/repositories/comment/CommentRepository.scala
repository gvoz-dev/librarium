package itcube.repositories.comment

import itcube.entities.Comment
import zio.*

/** Репозиторий пользовательских комментариев к книгам. */
trait CommentRepository:

  /** Добавить комментарий пользователя к книге.
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
  ): Task[Option[Comment]]

  /** Получить все комментарии пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def findByUser(userId: String): Task[List[Comment]]

  /** Получить все комментарии к книге.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByBook(bookId: String): Task[List[Comment]]

  /** Получить все комментарии пользователя к книге.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByUserAndBook(
      userId: String,
      bookId: String
  ): Task[List[Comment]]

  /** Изменить комментарий.
    *
    * @param comment
    *   комментарий
    */
  def update(comment: Comment): Task[Option[Comment]]

  /** Удалить комментарий.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def delete(id: String): Task[Unit]

end CommentRepository

object CommentRepository:

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
  ): ZIO[CommentRepository, Throwable, Option[Comment]] =
    ZIO.serviceWithZIO[CommentRepository](_.create(comment, userId, bookId))

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

  /** Сервис изменения комментария.
    *
    * @param comment
    *   комментарий
    */
  def update(
      comment: Comment
  ): ZIO[CommentRepository, Throwable, Option[Comment]] =
    ZIO.serviceWithZIO[CommentRepository](_.update(comment))

  /** Сервис удаления комментария.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def delete(id: String): ZIO[CommentRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[CommentRepository](_.delete(id))

end CommentRepository
