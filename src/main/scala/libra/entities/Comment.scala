package libra.entities

import zio.schema.*
import zio.schema.annotation.*
import zio.schema.validation.*

import java.time.LocalDateTime
import java.util.UUID

/** Сущность "Комментарий".
  *
  * @param id
  *   уникальный идентификатор
  * @param text
  *   текст комментария
  * @param isPrivate
  *   является или нет комментарий приватным
  * @param date
  *   дата и время комментария
  */
final case class Comment(
    @description("Comment ID")
    @optionalField
    id: Option[UUID],
    @description("Comment text")
    @validate(Validation.minLength(1))
    text: String,
    @description("Is the comment private?")
    isPrivate: Boolean,
    @description("Date of comment")
    @optionalField
    date: Option[LocalDateTime]
)

object Comment:

  /** Гивен ZIO-схемы комментария. */
  given Schema[Comment] = DeriveSchema.gen

end Comment
