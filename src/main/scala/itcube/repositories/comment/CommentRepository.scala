package itcube.repositories.comment

import itcube.entities.Comment
import zio.*

/** Репозиторий пользовательских комментариев к книгам. */
trait CommentRepository:

  /** Получить комментарий по ID.
    *
    * @param id
    *   уникальный идентификатор комментария
    */
  def findById(
      id: String
  ): Task[Option[Comment]]

  /** Получить все комментарии пользователя.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    */
  def findByUser(
      userId: String
  ): Task[List[Comment]]

  /** Получить все комментарии к книге.
    *
    * @param bookId
    *   уникальный идентификатор книги
    */
  def findByBook(
      bookId: String
  ): Task[List[Comment]]

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
      id: String
  ): Task[Unit]

end CommentRepository
