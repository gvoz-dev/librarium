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
    *   уникальный идентификатор пользователя (строка UUID).
    */
  def delete(id: String): Task[Unit]

end UserRepository
