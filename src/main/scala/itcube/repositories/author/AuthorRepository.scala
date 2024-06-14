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
    *   уникальный идентификатор автора (строка UUID).
    */
  def delete(id: String): Task[Unit]

end AuthorRepository
