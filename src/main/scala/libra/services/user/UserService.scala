package libra.services.user

import libra.entities.User
import libra.repositories.user.UserRepository
import libra.utils.ServiceError.*
import zio.ZIO

/** Сервис пользователей. */
object UserService:

  /** Получить всех пользователей. */
  def all: ZIO[UserRepository, RepositoryError, List[User]] =
    ZIO
      .serviceWithZIO[UserRepository](_.all)
      .mapError(e => DatabaseError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFoundError("Users not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти пользователя по ID.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[UserRepository, RepositoryError, User] =
    ZIO
      .serviceWithZIO[UserRepository](_.findById(id))
      .mapError(e => DatabaseError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case None       => ZIO.fail(NotFoundError(s"User not found by ID: $id"))
        case Some(user) => ZIO.succeed(user)
      }

  /** Найти пользователя по email.
    *
    * @param email
    *   адрес электронной почты
    */
  def findByEmail(
      email: String
  ): ZIO[UserRepository, RepositoryError, User] =
    ZIO
      .serviceWithZIO[UserRepository](_.findByEmail(email))
      .mapError(e => DatabaseError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case None => ZIO.fail(NotFoundError(s"User not found by email: $email"))
        case Some(user) => ZIO.succeed(user)
      }

  /** Получить пользователя по имени.
    *
    * @param name
    *   имя пользователя
    */
  def findByName(
      name: String
  ): ZIO[UserRepository, RepositoryError, List[User]] =
    ZIO
      .serviceWithZIO[UserRepository](_.findByName(name))
      .mapError(e => DatabaseError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFoundError(s"Users not found by name: $name"))
        case list => ZIO.succeed(list)
      }

  /** Создать пользователя.
    *
    * @param user
    *   пользователь
    */
  def create(
      user: User
  ): ZIO[UserRepository, DatabaseError, User] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.create(user))
        .mapError(e => DatabaseError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"User $user not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"User $result created")
    } yield result

  /** Изменить пользователя.
    *
    * @param user
    *   пользователь
    */
  def update(
      user: User
  ): ZIO[UserRepository, DatabaseError, User] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.update(user))
        .mapError(e => DatabaseError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"User $user not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"User $result updated")
    } yield result

  /** Удалить пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[UserRepository, DatabaseError, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.delete(id))
        .mapError(e => DatabaseError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"User ($id) not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"User ($id) deleted")
    } yield result

end UserService
