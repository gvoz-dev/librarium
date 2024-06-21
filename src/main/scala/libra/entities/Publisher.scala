package libra.entities

import zio.schema.*
import zio.schema.annotation.*

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
    @optionalField
    @description("Publisher ID")
    id: Option[UUID],
    @description("Publisher's name")
    name: String,
    @description("Publisher's country")
    country: String
)

object Publisher:

  /** Гивен ZIO-схемы издателя. */
  given Schema[Publisher] = DeriveSchema.gen

end Publisher
