package itcube.repositories.user

import io.getquill.*
import itcube.entities.User
import itcube.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource
import scala.util.Try

/** Реализация репозитория пользователей для СУБД PostgreSQL.
  *
  * @param ds
  *   источник данных [[DataSource]]
  */
case class PgUserRepository(ds: DataSource) extends UserRepository:

  import PostgresContext.*

  private val dsLayer: ULayer[DataSource] = ZLayer.succeed(ds)

  /** Преобразование строки таблицы в сущность "Пользователь". */
  private inline def toUser: Users => User =
    row =>
      User(
        Some(row.id),
        row.name,
        row.email,
        row.password,
        row.role
      )

  /** Преобразование сущности "Пользователь" в строку таблицы.
    *
    * @param id
    *   уникальный идентификатор пользователя
    * @param user
    *   пользователь
    */
  private inline def toUsersRow(id: UUID, user: User): Users =
    lift(
      Users(
        id,
        user.name,
        user.email,
        user.password,
        user.role
      )
    )

  /** Получить всех пользователей. */
  override def all: Task[List[User]] =
    run {
      quote {
        query[Users]
          .map(toUser)
      }
    }.provide(dsLayer)
  end all

  /** Получить пользователя по ID.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  override def findById(id: String): Task[Option[User]] =
    for {
      uuid <- ZIO.fromTry(Try(UUID.fromString(id)))
      result <- run {
        quote {
          query[Users]
            .filter(_.id == lift(uuid))
            .map(toUser)
        }
      }.map(_.headOption)
        .provide(dsLayer)
    } yield result
  end findById

  /** Получить пользователя по email.
    *
    * @param email
    *   адрес электронной почты
    */
  override def findByEmail(email: String): Task[Option[User]] =
    run {
      quote {
        query[Users]
          .filter(_.email == lift(email))
          .map(toUser)
      }
    }.map(_.headOption)
      .provide(dsLayer)
  end findByEmail

  /** Получить пользователя по имени.
    *
    * @param name
    *   имя пользователя
    */
  override def findByName(name: String): Task[Option[User]] =
    run {
      quote {
        query[Users]
          .filter(_.name == lift(name))
          .map(toUser)
      }
    }.map(_.headOption)
      .provide(dsLayer)
  end findByName

  /** Создать пользователя.
    *
    * @param user
    *   пользователь
    */
  override def create(user: User): Task[Option[User]] =
    for {
      id <- Random.nextUUID
      result <- run {
        quote {
          query[Users]
            .insertValue(toUsersRow(id, user))
            .returning(toUser)
        }
      }.option
        .provide(dsLayer)
    } yield result
  end create

  /** Изменить пользователя.
    *
    * @param user
    *   пользователь
    */
  override def update(user: User): Task[Option[User]] =
    for {
      id <- ZIO.getOrFail(user.id)
      result <- run {
        quote {
          query[Users]
            .filter(_.id == lift(id))
            .updateValue(toUsersRow(id, user))
            .returning(toUser)
        }
      }.option
        .provide(dsLayer)
    } yield result
  end update

  /** Удалить пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  override def delete(id: String): Task[Unit] =
    for {
      uuid <- ZIO.fromTry(Try(UUID.fromString(id)))
      result <- transaction {
        for {
          _ <- run {
            quote {
              query[UsersBooks]
                .filter(_.userId == lift(uuid))
                .delete
            }
          }
          _ <- run {
            quote {
              query[Users]
                .filter(_.id == lift(uuid))
                .delete
            }
          }
        } yield ()
      }.provide(dsLayer)
    } yield result
  end delete

end PgUserRepository

object PgUserRepository:

  /** Слой репозитория пользователей. */
  val live: ZLayer[Any, Throwable, PgUserRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgUserRepository(_))

end PgUserRepository
