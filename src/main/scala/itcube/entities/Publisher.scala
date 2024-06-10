package itcube.entities

import zio.schema.*

import java.util.UUID

/** Сущность "Издатель".
  *
  * @param id
  *   уникальный идентификатор
  * @param name
  *   название издательства
  * @param country
  *   страна
  */
final case class Publisher(
    id: Option[UUID],
    name: String,
    country: String
)

object Publisher:

  /** Гивен ZIO-схемы издателя. Выводится в автоматическом режиме.
    */
  given publisherSchema: Schema[Publisher] = DeriveSchema.gen[Publisher]

end Publisher
