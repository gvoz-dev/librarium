package itcube.services.user

import itcube.entities.User
import itcube.repositories.user.UserRepository
import zio.ZIO

/** Сервис пользователей. */
object UserService:

  /** Получить всех пользователей. */
  def all: ZIO[UserRepository, Throwable, List[User]] =
    ZIO.serviceWithZIO[UserRepository](_.all)

  /** Получить пользователя по ID.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.findById(id))

  /** Получить пользователя по email.
    *
    * @param email
    *   адрес электронной почты
    */
  def findByEmail(
      email: String
  ): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.findByEmail(email))

  /** Получить пользователя по имени.
    *
    * @param name
    *   имя пользователя
    */
  def findByName(
      name: String
  ): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.findByName(name))

  /** Создать пользователя.
    *
    * @param user
    *   пользователь
    */
  def create(
      user: User
  ): ZIO[UserRepository, Throwable, User] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.create(user))
        .onError(e =>
          ZIO.logError(s"User `$user` not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"User `$result` created")
    } yield result

  /** Изменить пользователя.
    *
    * @param user
    *   пользователь
    */
  def update(
      user: User
  ): ZIO[UserRepository, Throwable, User] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.update(user))
        .onError(e =>
          ZIO.logError(s"User `$user` not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"User `$result` updated")
    } yield result

  /** Удалить пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[UserRepository, Throwable, Unit] =
    for {
      result <- ZIO
        .serviceWithZIO[UserRepository](_.delete(id))
        .onError(e =>
          ZIO.logError(s"User `$id` not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"User `$id` deleted")
    } yield result

end UserService
