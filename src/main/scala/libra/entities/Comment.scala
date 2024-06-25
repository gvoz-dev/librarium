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
  * @param userId
  *   уникальный идентификатор пользователя
  * @param bookId
  *   уникальный идентификатор книги
  * @param text
  *   текст комментария
  * @param isPrivate
  *   является или нет комментарий приватным
  * @param time
  *   время публикации комментария
  * @param lastModifiedTime
  *   время последнего изменения
  */
final case class Comment(
    @description("Comment ID")
    @optionalField
    id: Option[UUID],
    @description("User ID")
    userId: UUID,
    @description("Book ID")
    bookId: UUID,
    @description("Comment text")
    @validate(Validation.minLength(1))
    text: String,
    @description("Is the comment private?")
    isPrivate: Boolean,
    @description("Post time")
    @optionalField
    time: Option[LocalDateTime],
    @description("Last modified time")
    @optionalField
    lastModifiedTime: Option[LocalDateTime]
)

object Comment:

  /** Гивен ZIO-схемы комментария. */
  given Schema[Comment] = DeriveSchema.gen

end Comment
