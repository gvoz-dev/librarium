package itcube.entities

import zio.schema.*

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
    id: Option[UUID],
    name: String,
    country: Option[String]
)

object Author:

  /** Гивен ZIO-схемы автора. Выводится в автоматическом режиме.
    */
  given authorSchema: Schema[Author] = DeriveSchema.gen[Author]

end Author
