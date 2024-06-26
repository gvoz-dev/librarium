package libra.repositories.author

import io.getquill.*
import libra.entities.Author
import libra.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource

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

  /** Найти автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора
    */
  override def findById(id: UUID): Task[Option[Author]] =
    run {
      quote {
        query[Authors]
          .filter(a => a.id == lift(id))
          .map(toAuthor)
      }
    }.map(_.headOption).provide(dsLayer)
  end findById

  /** Найти авторов по имени.
    *
    * @param name
    *   имя автора
    */
  override def findByName(name: String): Task[List[Author]] =
    run {
      quote {
        query[Authors]
          .filter(a => a.name == lift(name))
          .map(toAuthor)
      }
    }.provide(dsLayer)
  end findByName

  /** Создать автора.
    *
    * @param author
    *   автор
    */
  override def create(author: Author): Task[Author] =
    for {
      id     <- Random.nextUUID
      result <-
        run {
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
      id     <- ZIO.getOrFail(author.id)
      result <-
        run {
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
    *   уникальный идентификатор автора
    */
  override def delete(id: UUID): Task[Unit] =
    transaction {
      for {
        _ <-
          run {
            quote {
              query[BooksAuthors]
                .filter(ba => ba.authorId == lift(id))
                .delete
            }
          }
        _ <-
          run {
            quote {
              query[Authors]
                .filter(a => a.id == lift(id))
                .delete
            }
          }
      } yield ()
    }.provide(dsLayer)
  end delete

end PgAuthorRepository

object PgAuthorRepository:

  /** Слой репозитория авторов. */
  val live: ZLayer[Any, Throwable, PgAuthorRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgAuthorRepository(_))

end PgAuthorRepository
