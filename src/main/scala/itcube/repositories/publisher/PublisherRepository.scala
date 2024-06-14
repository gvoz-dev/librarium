package itcube.repositories.publisher

import itcube.entities.Publisher
import zio.*

/** Репозиторий издателей. */
trait PublisherRepository:

  /** Получить всех издателей. */
  def all: Task[List[Publisher]]

  /** Получить издателя по ID.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def findById(id: String): Task[Option[Publisher]]

  /** Получить издателя по названию.
    *
    * @param name
    *   название издателя
    */
  def findByName(name: String): Task[Option[Publisher]]

  /** Создать издателя.
    *
    * @param publisher
    *   издатель
    */
  def create(publisher: Publisher): Task[Publisher]

  /** Изменить издателя.
    *
    * @param publisher
    *   издатель
    */
  def update(publisher: Publisher): Task[Publisher]

  /** Удалить издателя.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def delete(id: String): Task[Unit]

end PublisherRepository
