package libra.entities

import zio.schema.*
import zio.schema.annotation.*

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
    @optionalField
    @description("User ID")
    id: Option[UUID],
    @description("User name")
    name: String,
    @description("User email")
    email: String,
    @transientField
    @description("User password")
    password: String,
    @optionalField
    @description("User role")
    role: String = "user"
)

object User:

  /** Гивен ZIO-схемы пользователя. */
  given Schema[User] = DeriveSchema.gen[User]

end User
