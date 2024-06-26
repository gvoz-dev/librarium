package libra.repositories.user

import io.getquill.*
import libra.entities.User
import libra.repositories.*
import zio.*

import java.util.UUID
import javax.sql.DataSource

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

  /** Найти пользователя по ID.
    *
    * @param id
    *   уникальный идентификатор пользователя
    */
  override def findById(id: UUID): Task[Option[User]] =
    run {
      quote {
        query[Users]
          .filter(u => u.id == lift(id))
          .map(toUser)
      }
    }.map(_.headOption).provide(dsLayer)
  end findById

  /** Найти пользователя по email.
    *
    * @param email
    *   адрес электронной почты
    */
  override def findByEmail(email: String): Task[Option[User]] =
    run {
      quote {
        query[Users]
          .filter(u => u.email == lift(email))
          .map(toUser)
      }
    }.map(_.headOption).provide(dsLayer)
  end findByEmail

  /** Найти пользователей по имени.
    *
    * @param name
    *   имя пользователя
    */
  override def findByName(name: String): Task[List[User]] =
    run {
      quote {
        query[Users]
          .filter(u => u.name == lift(name))
          .map(toUser)
      }
    }.provide(dsLayer)
  end findByName

  /** Создать пользователя.
    *
    * @param user
    *   пользователь
    */
  override def create(user: User): Task[User] =
    for {
      id     <- Random.nextUUID
      result <-
        run {
          quote {
            query[Users]
              .insertValue(toUsersRow(id, user))
              .returning(toUser)
          }
        }.provide(dsLayer)
    } yield result
  end create

  /** Изменить пользователя.
    *
    * @param user
    *   пользователь
    */
  override def update(user: User): Task[User] =
    for {
      id     <- ZIO.getOrFail(user.id)
      result <-
        run {
          quote {
            query[Users]
              .filter(u => u.id == lift(id))
              .updateValue(toUsersRow(id, user))
              .returning(toUser)
          }
        }.provide(dsLayer)
    } yield result
  end update

  /** Удалить пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя
    */
  override def delete(id: UUID): Task[Unit] =
    transaction {
      for {
        _ <-
          run {
            quote {
              query[UsersBooks]
                .filter(ub => ub.userId == lift(id))
                .delete
            }
          }
        _ <-
          run {
            quote {
              query[Users]
                .filter(u => u.id == lift(id))
                .delete
            }
          }
      } yield ()
    }.provide(dsLayer)
  end delete

end PgUserRepository

object PgUserRepository:

  /** Слой репозитория пользователей. */
  val live: ZLayer[Any, Throwable, PgUserRepository] =
    PostgresDataSource.live >>> ZLayer.fromFunction(PgUserRepository(_))

end PgUserRepository
