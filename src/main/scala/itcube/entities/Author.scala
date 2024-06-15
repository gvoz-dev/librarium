package itcube.entities

import zio.schema.*
import zio.schema.annotation.{description, optionalField}

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
    @description("Author's ID")
    id: Option[UUID],
    @description("Author's name")
    name: String,
    @optionalField
    @description("Author's country")
    country: Option[String]
)

object Author:

  /** Гивен ZIO-схемы автора. Выводится в автоматическом режиме.
    */
  given authorSchema: Schema[Author] = DeriveSchema.gen

end Author
