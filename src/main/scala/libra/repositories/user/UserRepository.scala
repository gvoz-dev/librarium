package libra.repositories.user

import libra.entities.User
import zio.*

import java.util.UUID

/** Репозиторий пользователей. */
trait UserRepository:

  /** Получить всех пользователей. */
  def all: Task[List[User]]

  /** Найти пользователя по ID.
    *
    * @param id
    *   уникальный идентификатор пользователя
    */
  def findById(id: UUID): Task[Option[User]]

  /** Найти пользователя по email.
    *
    * @param email
    *   адрес электронной почты
    */
  def findByEmail(email: String): Task[Option[User]]

  /** Найти пользователей по имени.
    *
    * @param name
    *   имя пользователя
    */
  def findByName(name: String): Task[List[User]]

  /** Создать пользователя.
    *
    * @param user
    *   пользователь
    */
  def create(user: User): Task[User]

  /** Изменить пользователя.
    *
    * @param user
    *   пользователь
    */
  def update(user: User): Task[User]

  /** Удалить пользователя.
    *
    * @param id
    *   уникальный идентификатор пользователя
    */
  def delete(id: UUID): Task[Unit]

end UserRepository
