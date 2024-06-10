package itcube.repositories.book

import itcube.entities.Book
import zio.*

/** Репозиторий книг. */
trait BookRepository:

  /** Получить все книги. */
  def all: Task[List[Book]]

  /** Получить книгу по ID.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def findById(id: String): Task[Option[Book]]

  /** Получить книгу по названию.
    *
    * @param title
    *   название книги
    */
  def findByTitle(title: String): Task[Option[Book]]

  /** Создать книгу.
    *
    * @param book
    *   книга
    */
  def create(book: Book): Task[Option[Book]]

  /** Изменить книгу.
    *
    * @param book
    *   книга
    */
  def update(book: Book): Task[Option[Book]]

  /** Удалить книгу.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def delete(id: String): Task[Unit]

end BookRepository

object BookRepository:

  /** Сервис получения всех книг. */
  def all: ZIO[BookRepository, Throwable, List[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.all)

  /** Сервис получения книги по ID.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def findById(id: String): ZIO[BookRepository, Throwable, Option[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.findById(id))

  /** Сервис получения книги по названию.
    *
    * @param title
    *   название книги
    */
  def findByTitle(title: String): ZIO[BookRepository, Throwable, Option[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.findByTitle(title))

  /** Сервис создания книги.
    *
    * @param book
    *   книга
    */
  def create(book: Book): ZIO[BookRepository, Throwable, Option[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.create(book))

  /** Сервис изменения книги.
    *
    * @param book
    *   книга
    */
  def update(book: Book): ZIO[BookRepository, Throwable, Option[Book]] =
    ZIO.serviceWithZIO[BookRepository](_.update(book))

  /** Сервис удаления книги.
    *
    * @param id
    *   уникальный идентификатор книги (строка UUID).
    */
  def delete(id: String): ZIO[BookRepository, Throwable, Unit] =
    ZIO.serviceWithZIO[BookRepository](_.delete(id))

end BookRepository
