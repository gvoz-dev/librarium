package itcube.repositories.author

import io.getquill.*
import itcube.entities.Author
import itcube.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource
import scala.util.Try

/** Реализация репозитория авторов для СУБД PostgreSQL.
  *
  * @param ds
  *   источник данных [[DataSource]]
  */
case class PgAuthorRepository(ds: DataSource) extends AuthorRepository:

  import PostgresContext.*

  private val dsLayer: ULayer[DataSource] = ZLayer.succeed(ds)

  /** Преобразование строки таблицы в сущность "Автор". */
  private inline def toAuthor: Authors => Author =
    row =>
      Author(
        Some(row.id),
        row.name,
        row.country
      )

  /** Преобразование сущности "Автор" в строку таблицы.
    *
    * @param id
    *   уникальный идентификатор автора
    * @param author
    *   автор
    */
  private inline def toAuthorsRow(id: UUID, author: Author): Authors =
    lift(
      Authors(
        id,
        author.name,
        author.country
      )
    )

  /** Получить всех авторов. */
  override def all: Task[List[Author]] =
    run {
      quote {
        query[Authors]
          .map(toAuthor)
      }
    }.provide(dsLayer)
  end all

  /** Получить автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  override def findById(id: String): Task[Option[Author]] =
    for {
      uuid <- ZIO.fromTry(Try(UUID.fromString(id)))
      result <- run {
        quote {
          query[Authors]
            .filter(a => a.id == lift(uuid))
            .map(toAuthor)
        }
      }.map(_.headOption).provide(dsLayer)
    } yield result
  end findById

  /** Получить автора по имени.
    *
    * @param name
    *   имя автора
    */
  override def findByName(name: String): Task[Option[Author]] =
    run {
      quote {
        query[Authors]
          .filter(a => a.name == lift(name))
          .map(toAuthor)
      }
    }.map(_.headOption).provide(dsLayer)
  end findByName

  /** Создать автора.
    *
    * @param author
    *   автор
    */
  override def create(author: Author): Task[Author] =
    for {
      id <- Random.nextUUID
      result <- run {
        quote {
          query[Authors]
            .insertValue(toAuthorsRow(id, author))
            .returning(toAuthor)
        }
      }.provide(dsLayer)
    } yield result
  end create

  /** Изменить автора.
    *
    * @param author
    *   автор
    */
  override def update(author: Author): Task[Author] =
    for {
      id <- ZIO.getOrFail(author.id)
      result <- run {
        quote {
          query[Authors]
            .filter(a => a.id == lift(id))
            .updateValue(toAuthorsRow(id, author))
            .returning(toAuthor)
        }
      }.provide(dsLayer)
    } yield result
  end update

  /** Удалить автора.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  override def delete(id: String): Task[Unit] =
    for {
      uuid <- ZIO.fromTry(Try(UUID.fromString(id)))
      result <- transaction {
        for {
          _ <- run {
            quote {
              query[BooksAuthors]
                .filter(ba => ba.authorId == lift(uuid))
                .delete
            }
          }
          _ <- run {
            quote {
              query[Authors]
                .filter(a => a.id == lift(uuid))
                .delete
            }
          }
        } yield ()
      }.provide(dsLayer)
    } yield result
  end delete

end PgAuthorRepository

object PgAuthorRepository:

  /** Слой репозитория авторов. */
  val live: ZLayer[Any, Throwable, PgAuthorRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgAuthorRepository(_))

end PgAuthorRepository
