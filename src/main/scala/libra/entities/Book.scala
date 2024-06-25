package libra.entities

import zio.schema.*
import zio.schema.annotation.*
import zio.schema.validation.*

import java.util.UUID

/** Сущность "Книга".
  *
  * @param id
  *   уникальный идентификатор
  * @param title
  *   название книги
  * @param isbn
  *   международный стандартный книжный номер (10-значный)
  * @param isbn13
  *   международный стандартный книжный номер (13-значный)
  * @param edition
  *   издание
  * @param year
  *   год издания
  * @param pages
  *   количество страниц
  * @param image
  *   изображение обложки
  * @param description
  *   описание книги
  * @param language
  *   язык текста
  * @param category
  *   категория
  * @param publisher
  *   издатель
  * @param author
  *   автор
  */
final case class Book(
    @description("Book ID")
    @optionalField
    id: Option[UUID],
    @description("Book title")
    @validate(Validation.minLength(1))
    title: String,
    @description("Book ISBN10")
    @optionalField
    @validate((Validation.minLength(10) && Validation.maxLength(10)).optional(true))
    isbn: Option[String],
    @description("Book ISBN13")
    @optionalField
    @validate((Validation.minLength(13) && Validation.maxLength(13)).optional(true))
    isbn13: Option[String],
    @description("Book edition")
    @optionalField
    @validate(Validation.minLength(1).optional(true))
    edition: Option[String],
    @description("Year of publication")
    @optionalField
    year: Option[Int],
    @description("Number of pages")
    @optionalField
    pages: Option[Int],
    @description("Cover image")
    @optionalField
    @validate(Validation.minLength(1).optional(true))
    image: Option[String],
    @description("Book description")
    @optionalField
    @validate(Validation.minLength(1).optional(true))
    description: Option[String],
    @description("Book language")
    @optionalField
    @validate(Validation.minLength(1).optional(true))
    language: Option[String],
    @description("Book category")
    @optionalField
    @validate(Validation.minLength(1).optional(true))
    category: Option[String],
    @description("Book publisher")
    @optionalField
    publisher: Option[Publisher],
    @description("Book author")
    @optionalField
    author: Option[Author]
)

object Book:

  /** Гивен ZIO-схемы книги. */
  given Schema[Book] = DeriveSchema.gen

end Book
