package libra.repositories.author

import libra.entities.Author
import zio.*

import java.util.UUID

/** Репозиторий авторов. */
trait AuthorRepository:

  /** Получить всех авторов. */
  def all: Task[List[Author]]

  /** Найти автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора
    */
  def findById(id: UUID): Task[Option[Author]]

  /** Найти авторов по имени.
    *
    * @param name
    *   имя автора
    */
  def findByName(name: String): Task[List[Author]]

  /** Создать автора.
    *
    * @param author
    *   автор
    */
  def create(author: Author): Task[Author]

  /** Изменить автора.
    *
    * @param author
    *   автор
    */
  def update(author: Author): Task[Author]

  /** Удалить автора.
    *
    * @param id
    *   уникальный идентификатор автора
    */
  def delete(id: UUID): Task[Unit]

end AuthorRepository
