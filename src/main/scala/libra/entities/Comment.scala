package libra.entities

import zio.schema.*
import zio.schema.annotation.*

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
    @optionalField
    @description("Comment ID")
    id: Option[UUID],
    @description("Comment text")
    text: String,
    @description("Is the comment private?")
    isPrivate: Boolean,
    @optionalField
    @description("Date of comment")
    date: Option[LocalDateTime]
)

object Comment:

  /** Гивен ZIO-схемы комментария. */
  given Schema[Comment] = DeriveSchema.gen

end Comment
