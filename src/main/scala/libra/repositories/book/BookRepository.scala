package libra.repositories.book

import libra.entities.Book
import zio.*

import java.util.UUID

/** Репозиторий книг. */
trait BookRepository:

  /** Получить все книги. */
  def all: Task[List[Book]]

  /** Найти книгу по ID.
    *
    * @param id
    *   уникальный идентификатор книги
    */
  def findById(id: UUID): Task[Option[Book]]

  /** Найти книги по названию.
    *
    * @param title
    *   название книги
    */
  def findByTitle(title: String): Task[List[Book]]

  /** Создать книгу.
    *
    * @param book
    *   книга
    */
  def create(book: Book): Task[Book]

  /** Изменить книгу.
    *
    * @param book
    *   книга
    */
  def update(book: Book): Task[Book]

  /** Удалить книгу.
    *
    * @param id
    *   уникальный идентификатор книги
    */
  def delete(id: UUID): Task[Unit]

end BookRepository
