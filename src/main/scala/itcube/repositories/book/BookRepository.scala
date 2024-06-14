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
    *   уникальный идентификатор книги (строка UUID).
    */
  def delete(id: String): Task[Unit]

end BookRepository
