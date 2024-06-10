package itcube.repositories.user

import itcube.entities.User
import zio.*

/** Репозиторий пользователей. */
trait UserRepository:

  /** Получить всех пользователей. */
  def all: Task[List[User]]

  /** Получить пользователя по ID.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def findById(id: String): Task[Option[User]]

  /** Получить пользователя по email.
    *
    * @param email
    *   адрес электронной почты
    */
  def findByEmail(email: String): Task[Option[User]]

  /** Получить пользователя по имени.
    *
    * @param name
    *   имя пользователя
    */
  def findByName(name: String): Task[Option[User]]

  /** Создать пользователя.
    *
    * @param user
    *   пользователь
    */
  def create(user: User): Task[Option[User]]

  /** Изменить пользователя.
    *
    * @param user
    *   пользователь
    */
  def update(user: User): Task[Option[User]]

  /** Удалить пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def delete(id: String): Task[Unit]

end UserRepository

object UserRepository:

  /** Сервис получения всех пользователей. */
  def all: ZIO[UserRepository, Throwable, List[User]] =
    ZIO.serviceWithZIO[UserRepository](_.all)

  /** Сервис получения пользователя по ID.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def findById(id: String): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.findById(id))

  /** Сервис получения пользователя по email.
    *
    * @param email
    *   адрес электронной почты
    */
  def findByEmail(email: String): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.findByEmail(email))

  /** Сервис получения пользователя по имени.
    *
    * @param name
    *   имя пользователя
    */
  def findByName(name: String): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.findByName(name))

  /** Сервис создания пользователя.
    *
    * @param user
    *   пользователь
    */
  def create(user: User): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.create(user))

  /** Сервис изменения пользователя.
    *
    * @param user
    *   пользователь
    */
  def update(user: User): ZIO[UserRepository, Throwable, Option[User]] =
    ZIO.serviceWithZIO[UserRepository](_.update(user))

  /** Сервис удаления пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def delete(id: String): ZIO[UserRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[UserRepository](_.delete(id))

end UserRepository
