package libra.repositories.publisher

import io.getquill.*
import libra.entities.Publisher
import libra.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource
import scala.util.Try

/** Реализация репозитория издателей для СУБД PostgreSQL.
  *
  * @param ds
  *   источник данных [[DataSource]]
  */
case class PgPublisherRepository(ds: DataSource) extends PublisherRepository:

  import PostgresContext.*

  private val dsLayer: ULayer[DataSource] = ZLayer.succeed(ds)

  /** Преобразование строки таблицы в сущность "Издатель". */
  private inline def toPublisher: Publishers => Publisher =
    row =>
      Publisher(
        Some(row.id),
        row.name,
        row.country
      )

  /** Преобразование сущности "Издатель" в строку таблицы.
    *
    * @param id
    *   уникальный идентификатор автора
    * @param publisher
    *   издатель
    */
  private inline def toPublishersRow(
      id: UUID,
      publisher: Publisher
  ): Publishers =
    lift(
      Publishers(
        id,
        publisher.name,
        publisher.country
      )
    )

  /** Получить всех издателей. */
  override def all: Task[List[Publisher]] =
    run {
      quote {
        query[Publishers]
          .map(toPublisher)
      }
    }.provide(dsLayer)
  end all

  /** Найти издателя по ID.
    *
    * @param id
    *   уникальный идентификатор издателя
    */
  override def findById(id: UUID): Task[Option[Publisher]] =
    run {
      quote {
        query[Publishers]
          .filter(p => p.id == lift(id))
          .map(toPublisher)
      }
    }.map(_.headOption).provide(dsLayer)
  end findById

  /** Найти издателя по названию.
    *
    * @param name
    *   название издателя
    */
  override def findByName(name: String): Task[List[Publisher]] =
    run {
      quote {
        query[Publishers]
          .filter(p => p.name == lift(name))
          .map(toPublisher)
      }
    }.provide(dsLayer)
  end findByName

  /** Создать издателя.
    *
    * @param publisher
    *   издатель
    */
  override def create(publisher: Publisher): Task[Publisher] =
    for {
      id     <- Random.nextUUID
      result <-
        run {
          quote {
            query[Publishers]
              .insertValue(toPublishersRow(id, publisher))
              .returning(toPublisher)
          }
        }.provide(dsLayer)
    } yield result
  end create

  /** Изменить издателя.
    *
    * @param publisher
    *   издатель
    */
  override def update(publisher: Publisher): Task[Publisher] =
    for {
      id     <- ZIO.getOrFail(publisher.id)
      result <-
        run {
          quote {
            query[Publishers]
              .filter(p => p.id == lift(id))
              .updateValue(toPublishersRow(id, publisher))
              .returning(toPublisher)
          }
        }.provide(dsLayer)
    } yield result
  end update

  /** Удалить издателя.
    *
    * @param id
    *   уникальный идентификатор издателя
    */
  override def delete(id: UUID): Task[Unit] =
    transaction {
      run {
        quote {
          query[Publishers]
            .filter(p => p.id == lift(id))
            .delete
        }
      }
    }.unit.provide(dsLayer)
  end delete

end PgPublisherRepository

object PgPublisherRepository:

  /** Слой репозитория издателей. */
  val live: ZLayer[Any, Throwable, PgPublisherRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgPublisherRepository(_))

end PgPublisherRepository
