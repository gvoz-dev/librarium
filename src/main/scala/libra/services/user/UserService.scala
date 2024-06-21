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
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Users not found"))
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
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None       => ZIO.fail(NotFound(s"User not found by ID: $id"))
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
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None => ZIO.fail(NotFound(s"User not found by email: $email"))
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
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound(s"Users not found by name: $name"))
        case list => ZIO.succeed(list)
      }

  /** Создать пользователя.
    *
    * @param user
    *   пользователь
    */
  def create(
      user: User
  ): ZIO[UserRepository, InternalServerError, User] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.create(user))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$user not created:\n${e.prettyPrint}"))
      _ <- ZIO.logInfo(s"$result created")
    } yield result

  /** Изменить пользователя.
    *
    * @param user
    *   пользователь
    */
  def update(
      user: User
  ): ZIO[UserRepository, InternalServerError, User] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.update(user))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$user not updated:\n${e.prettyPrint}"))
      _ <- ZIO.logInfo(s"$result updated")
    } yield result

  /** Удалить пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[UserRepository, InternalServerError, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.delete(id))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"User ($id) not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"User ($id) deleted")
    } yield result

end UserService
