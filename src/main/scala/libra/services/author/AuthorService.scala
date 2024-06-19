package libra.services.author

import libra.entities.Author
import libra.repositories.author.AuthorRepository
import libra.services.*
import libra.utils.ServiceError.*
import zio.ZIO

/** Сервис авторов. */
object AuthorService:

  /** Получить всех авторов. */
  def all: ZIO[AuthorRepository, RepositoryError, List[Author]] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.all)
      .mapError(e => DatabaseError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFoundError("Authors not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[AuthorRepository, RepositoryError, Author] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.findById(id))
      .mapError(e => DatabaseError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case None => ZIO.fail(NotFoundError(s"Author not found by ID: $id"))
        case Some(author) => ZIO.succeed(author)
      }

  /** Найти авторов по имени.
    *
    * @param name
    *   имя автора
    */
  def findByName(
      name: String
  ): ZIO[AuthorRepository, RepositoryError, List[Author]] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.findByName(name))
      .mapError(e => DatabaseError(e.getMessage))
      .onError(e => ZIO.logError(s"Database error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil => ZIO.fail(NotFoundError(s"Authors not found by name: $name"))
        case list => ZIO.succeed(list)
      }

  /** Создать автора.
    *
    * @param author
    *   автор
    */
  def create(
      author: Author
  ): ZIO[AuthorRepository, DatabaseError, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.create(author))
        .mapError(e => DatabaseError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"Author $author not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Author $result created")
    } yield result

  /** Изменить автора.
    *
    * @param author
    *   автор
    */
  def update(
      author: Author
  ): ZIO[AuthorRepository, DatabaseError, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.update(author))
        .mapError(e => DatabaseError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"Author $author not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Author $result updated")
    } yield result

  /** Удалить автора.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[AuthorRepository, DatabaseError, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[AuthorRepository](_.delete(id))
        .mapError(e => DatabaseError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"Author ($id) not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Author ($id) deleted")
    } yield ()

end AuthorService
