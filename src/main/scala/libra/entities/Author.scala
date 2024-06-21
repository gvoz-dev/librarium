package libra.entities

import zio.schema.*
import zio.schema.annotation.*

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
    @optionalField
    @description("Author ID")
    id: Option[UUID],
    @description("Author's name")
    name: String,
    @optionalField
    @description("Author's country")
    country: Option[String]
)

object Author:

  /** Гивен ZIO-схемы автора. */
  given Schema[Author] = DeriveSchema.gen

end Author
