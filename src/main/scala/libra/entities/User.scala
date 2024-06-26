package libra.entities

import zio.schema.*
import zio.schema.annotation.*
import zio.schema.validation.*

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
    @description("User ID")
    @optionalField
    id: Option[UUID],
    @description("User name")
    @validate(Validation.minLength(1))
    name: String,
    @description("User email")
    @validate(Validation.email) // Нечитаемое сообщение об ошибке
    email: String,
    @description("User password")
    // @transientField // TODO: В генерируемом SwaggerUI ломается пример
    @validate(Validation.minLength(1))
    password: String,
    @description("User role")
    @optionalField
    @validate(Validation.minLength(1).optional(true))
    role: String = "user"
)

object User:

  /** Гивен ZIO-схемы пользователя. */
  given Schema[User] = DeriveSchema.gen[User]

end User
