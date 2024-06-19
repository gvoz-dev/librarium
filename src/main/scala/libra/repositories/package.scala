package libra

import io.getquill.*
import io.getquill.jdbczio.Quill
import zio.ZLayer

import java.time.LocalDateTime
import java.util.UUID
import javax.sql.DataSource

package object repositories:

  /** Источник данных [[DataSource]] PostgreSQL. */
  object PostgresDataSource:

    /** Слой источника данных. */
    def live: ZLayer[Any, Throwable, DataSource] =
      Quill.DataSource.fromPrefix("PgDataSource").orDie

  end PostgresDataSource

  /** Quill JDBC-контекст для СУБД PostgreSQL. */
  object PostgresContext extends PostgresZioJdbcContext(Escape)

  // Data-классы записей в таблицах БД (см. db/migration/V1_00__init.sql):

  /** Запись (строка) в таблице "Издатели".
    *
    * @param id
    *   уникальный идентификатор
    * @param name
    *   название издательства
    * @param country
    *   страна
    */
  final case class Publishers(
      id: UUID,
      name: String,
      country: String
  )

  /** Запись (строка) в таблице "Авторы".
    *
    * @param id
    *   уникальный идентификатор
    * @param name
    *   имя автора
    * @param country
    *   страна автора
    */
  final case class Authors(
      id: UUID,
      name: String,
      country: Option[String]
  )

  /** Запись (строка) в таблице "Книги".
    *
    * @param id
    *   уникальный идентификатор книги
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
    * @param publisherId
    *   уникальный идентификатор издателя
    */
  final case class Books(
      id: UUID,
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
      publisherId: Option[UUID]
  )

  /** Запись (строка) в таблице "Книги-Авторы".
    *
    * @param bookId
    *   уникальный идентификатор книги
    * @param authorId
    *   уникальный идентификатор автора
    */
  final case class BooksAuthors(
      bookId: UUID,
      authorId: UUID
  )

  /** Запись (строка) в таблице "Пользователи".
    *
    * @param id
    *   уникальный идентификатор
    * @param name
    *   имя пользователя
    * @param email
    *   адрес электронной почты
    * @param password
    *   пароль
    * @param role
    *   роль пользователя
    */
  final case class Users(
      id: UUID,
      name: String,
      email: String,
      password: String,
      role: String
  )

  /** Запись (строка) в таблице "Пользователи-Книги".
    *
    * @param id
    *   уникальный идентификатор отношения "Пользователь-Книга"
    * @param userId
    *   уникальный идентификатор пользователя
    * @param bookId
    *   уникальный идентификатор книги
    * @param inLibrary
    *   находится или нет книга в библиотеке пользователя
    * @param progress
    *   прогресс прочитанного пользователем (%)
    * @param rating
    *   пользовательский рейтинг книги
    */
  final case class UsersBooks(
      id: UUID,
      userId: UUID,
      bookId: UUID,
      inLibrary: Boolean,
      progress: Float,
      rating: Int
  )

  /** Запись (строка) в таблице "Комментарии".
    *
    * @param id
    *   уникальный идентификатор комментария
    * @param userBookId
    *   уникальный идентификатор отношения "Пользователь-Книга"
    * @param text
    *   текст комментария
    * @param isPrivate
    *   является или нет комментарий приватным
    * @param date
    *   дата добавления комментария
    */
  final case class Comments(
      id: UUID,
      userBookId: UUID,
      text: String,
      isPrivate: Boolean,
      date: LocalDateTime
  )

end repositories
