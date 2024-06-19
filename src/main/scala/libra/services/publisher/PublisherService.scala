package libra.services.publisher

import libra.entities.Publisher
import libra.repositories.publisher.PublisherRepository
import zio.ZIO

/** Сервис издателей. */
object PublisherService:

  /** Получить всех издателей. */
  def all: ZIO[PublisherRepository, Throwable, List[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.all)

  /** Получить издателя по ID.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[PublisherRepository, Throwable, Option[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.findById(id))

  /** Получить издателя по названию.
    *
    * @param name
    *   название издателя
    */
  def findByName(
      name: String
  ): ZIO[PublisherRepository, Throwable, Option[Publisher]] =
    ZIO.serviceWithZIO[PublisherRepository](_.findByName(name))

  /** Создать издателя.
    *
    * @param publisher
    *   издатель
    */
  def create(
      publisher: Publisher
  ): ZIO[PublisherRepository, Throwable, Publisher] =
    for {
      result <- ZIO
        .serviceWithZIO[PublisherRepository](_.create(publisher))
        .onError(e =>
          ZIO.logError(s"Publisher `$publisher` not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Publisher `$result` created")
    } yield result

  /** Изменить издателя.
    *
    * @param publisher
    *   издатель
    */
  def update(
      publisher: Publisher
  ): ZIO[PublisherRepository, Throwable, Publisher] =
    for {
      result <- ZIO
        .serviceWithZIO[PublisherRepository](_.update(publisher))
        .onError(e =>
          ZIO.logError(s"Publisher `$publisher` not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Publisher `$result` updated")
    } yield result

  /** Удалить издателя.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[PublisherRepository, Throwable, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[PublisherRepository](_.delete(id))
        .onError(e =>
          ZIO.logError(s"Publisher `$id` not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Publisher `$id` deleted")
    } yield ()

end PublisherService
