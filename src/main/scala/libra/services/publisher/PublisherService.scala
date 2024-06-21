package libra.services.publisher

import libra.entities.Publisher
import libra.repositories.publisher.PublisherRepository
import libra.utils.ServiceError.*
import zio.ZIO

/** Сервис издателей. */
object PublisherService:

  /** Получить всех издателей. */
  def all: ZIO[PublisherRepository, RepositoryError, List[Publisher]] =
    ZIO
      .serviceWithZIO[PublisherRepository](_.all)
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Publishers not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти издателя по ID.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[PublisherRepository, RepositoryError, Publisher] =
    ZIO
      .serviceWithZIO[PublisherRepository](_.findById(id))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None => ZIO.fail(NotFound(s"Publisher not found by ID: $id"))
        case Some(author) => ZIO.succeed(author)
      }

  /** Найти издателя по названию.
    *
    * @param name
    *   название издателя
    */
  def findByName(
      name: String
  ): ZIO[PublisherRepository, RepositoryError, List[Publisher]] =
    ZIO
      .serviceWithZIO[PublisherRepository](_.findByName(name))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound(s"Publishers not found by name: $name"))
        case list => ZIO.succeed(list)
      }

  /** Создать издателя.
    *
    * @param publisher
    *   издатель
    */
  def create(
      publisher: Publisher
  ): ZIO[PublisherRepository, InternalServerError, Publisher] =
    for {
      result <- ZIO
        .serviceWithZIO[PublisherRepository](_.create(publisher))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"$publisher not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"$result created")
    } yield result

  /** Изменить издателя.
    *
    * @param publisher
    *   издатель
    */
  def update(
      publisher: Publisher
  ): ZIO[PublisherRepository, InternalServerError, Publisher] =
    for {
      result <- ZIO
        .serviceWithZIO[PublisherRepository](_.update(publisher))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"$publisher not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"$result updated")
    } yield result

  /** Удалить издателя.
    *
    * @param id
    *   уникальный идентификатор издателя (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[PublisherRepository, InternalServerError, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[PublisherRepository](_.delete(id))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"Publisher ($id) not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Publisher ($id) deleted")
    } yield ()

end PublisherService
