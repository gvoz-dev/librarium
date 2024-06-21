package libra.entities

import zio.schema.*
import zio.schema.annotation.*

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
    @optionalField
    @description("Book ID")
    id: Option[UUID],
    @description("Book title")
    title: String,
    @optionalField
    @description("Book ISBN10")
    isbn: Option[String],
    @optionalField
    @description("Book ISBN13")
    isbn13: Option[String],
    @optionalField
    @description("Book edition")
    edition: Option[String],
    @optionalField
    @description("Year of publication")
    year: Option[Int],
    @optionalField
    @description("Number of pages")
    pages: Option[Int],
    @optionalField
    @description("Cover image")
    image: Option[String],
    @optionalField
    @description("Book description")
    description: Option[String],
    @optionalField
    @description("Book language")
    language: Option[String],
    @optionalField
    @description("Book category")
    category: Option[String],
    @optionalField
    @description("Book publisher")
    publisher: Option[Publisher],
    @optionalField
    @description("Book author")
    author: Option[Author]
)

object Book:

  /** Гивен ZIO-схемы книги. */
  given Schema[Book] = DeriveSchema.gen

end Book
