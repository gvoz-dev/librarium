package itcube.repositories.author

import itcube.entities.Author
import zio.*

/** Репозиторий авторов. */
trait AuthorRepository:

  /** Получить всех авторов. */
  def all: Task[List[Author]]

  /** Получить автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def findById(id: String): Task[Option[Author]]

  /** Получить автора по имени.
    *
    * @param name
    *   имя автора
    */
  def findByName(name: String): Task[Option[Author]]

  /** Создать автора.
    *
    * @param author
    *   автор
    */
  def create(author: Author): Task[Option[Author]]

  /** Изменить автора.
    *
    * @param author
    *   автор
    */
  def update(author: Author): Task[Option[Author]]

  /** Удалить автора.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def delete(id: String): Task[Unit]

end AuthorRepository

object AuthorRepository:

  /** Сервис получения всех авторов. */
  def all: ZIO[AuthorRepository, Throwable, List[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.all)

  /** Сервис получения автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def findById(id: String): ZIO[AuthorRepository, Throwable, Option[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.findById(id))

  /** Сервис получения автора по имени.
    *
    * @param name
    *   имя автора
    */
  def findByName(
      name: String
  ): ZIO[AuthorRepository, Throwable, Option[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.findByName(name))

  /** Сервис создания автора.
    *
    * @param author
    *   автор
    */
  def create(author: Author): ZIO[AuthorRepository, Throwable, Option[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.create(author))

  /** Сервис изменения автора.
    *
    * @param author
    *   автор
    */
  def update(author: Author): ZIO[AuthorRepository, Throwable, Option[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.update(author))

  /** Сервис удаления автора.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def delete(id: String): ZIO[AuthorRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[AuthorRepository](_.delete(id))

end AuthorRepository
