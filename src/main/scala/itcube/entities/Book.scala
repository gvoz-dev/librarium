package itcube.entities

import zio.schema.*

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
    id: Option[UUID],
    title: String,
    isbn: Option[String],
    isbn13: Option[String],
    edition: Option[String],
    year: Option[Int],
    pages: Option[Int],
    image: Option[String],
    description: Option[String],
    language: Option[String],
    category: Option[String],
    publisher: Option[Publisher],
    author: Option[Author]
)

object Book:

  /** Гивен ZIO-схемы книги. Выводится в автоматическом режиме.
    */
  given bookSchema: Schema[Book] = DeriveSchema.gen[Book]

end Book
