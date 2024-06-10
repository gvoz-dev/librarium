package itcube.entities

import zio.schema.*

import java.util.UUID

/** Сущность "Пользователь".
  *
  * @param id
  *   уникальный идентификатор
  * @param name
  *   имя пользователя
  * @param email
  *   почта
  * @param password
  *   пароль
  * @param role
  *   роль пользователя
  */
final case class User(
    id: Option[UUID],
    name: String,
    email: String,
    password: String,
    role: String
)

object User:

  /** Гивен ZIO-схемы пользователя. Выводится в автоматическом режиме.
    */
  given userSchema: Schema[User] = DeriveSchema.gen[User]

end User
