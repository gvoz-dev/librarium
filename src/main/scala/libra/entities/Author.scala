package libra.entities

import zio.schema.*
import zio.schema.annotation.*
import zio.schema.validation.*

import java.util.UUID

/** Сущность "Автор".
  *
  * @param id
  *   уникальный идентификатор
  * @param name
  *   имя автора
  * @param country
  *   страна автора
  */
final case class Author(
    @description("Author ID")
    @optionalField
    id: Option[UUID],
    @description("Author's name")
    @validate(Validation.minLength(1))
    name: String,
    @description("Author's country")
    @optionalField
    @validate(Validation.minLength(1).optional(true))
    country: Option[String]
)

object Author:

  /** Гивен ZIO-схемы автора. */
  given Schema[Author] = DeriveSchema.gen

end Author
