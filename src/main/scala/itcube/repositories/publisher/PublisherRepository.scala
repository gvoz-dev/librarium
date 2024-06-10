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
    *   имя издателя
    */
  def findByName(name: String): Task[Option[Publisher]]

  /** Создать издателя.
    *
    * @param publisher
    *   издатель
    */
  def create(publisher: Publisher): Task[Option[Publisher]]

  /** Изменить издателя.
    *
    * @param publisher
    *   издатель
    */
  def update(publisher: Publisher): Task[Option[Publisher]]

  /** Удалить издателя.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def delete(id: String): Task[Unit]

end PublisherRepository

object PublisherRepository:

  /** Сервис получения всех издателей. */
  def all: ZIO[PublisherRepository, Throwable, List[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.all)

  /** Сервис получения издателя по ID.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[PublisherRepository, Throwable, Option[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.findById(id))

  /** Сервис получения автора по имени.
    *
    * @param name
    *   имя издателя
    */
  def findByName(
      name: String
  ): ZIO[PublisherRepository, Throwable, Option[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.findByName(name))

  /** Сервис создания издателя.
    *
    * @param publisher
    *   издатель
    */
  def create(
      publisher: Publisher
  ): ZIO[PublisherRepository, Throwable, Option[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.create(publisher))

  /** Сервис изменения издателя.
    *
    * @param publisher
    *   издатель
    */
  def update(
      publisher: Publisher
  ): ZIO[PublisherRepository, Throwable, Option[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.update(publisher))

  /** Сервис удаления издателя.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def delete(id: String): ZIO[PublisherRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[PublisherRepository](_.delete(id))

end PublisherRepository
