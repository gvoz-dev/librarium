package libra.repositories.comment

import libra.entities.Comment
import zio.*

import java.util.UUID

/** Репозиторий пользовательских комментариев к книгам. */
trait CommentRepository:

  /** Получить все комментарии. */
  def all: Task[List[Comment]]

  /** Найти комментарий по ID.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def findById(
      id: UUID
  ): Task[Option[Comment]]

  /** Найти все комментарии пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def findByUser(
      userId: UUID
  ): Task[List[Comment]]

  /** Найти все комментарии к книге.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByBook(
      bookId: UUID
  ): Task[List[Comment]]

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
  ): Task[List[Comment]]

  /** Добавить комментарий пользователя к книге.
    *
    * @param comment
    *   комментарий
    */
  def create(
      comment: Comment
  ): Task[Comment]

  /** Изменить комментарий.
    *
    * @param comment
    *   комментарий
    */
  def update(
      comment: Comment
  ): Task[Comment]

  /** Удалить комментарий.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def delete(
      id: UUID
  ): Task[Unit]

end CommentRepository
