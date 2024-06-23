package libra.entities

import zio.schema.*
import zio.schema.annotation.*
import zio.schema.validation.*

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
    @description("Publisher ID")
    @optionalField
    id: Option[UUID],
    @description("Publisher's name")
    @validate(Validation.minLength(1))
    name: String,
    @description("Publisher's country")
    @validate(Validation.minLength(1))
    country: String
)

object Publisher:

  /** Гивен ZIO-схемы издателя. */
  given Schema[Publisher] = DeriveSchema.gen

end Publisher
