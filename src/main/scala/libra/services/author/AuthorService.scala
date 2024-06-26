package libra.services.author

import libra.entities.Author
import libra.repositories.author.AuthorRepository
import libra.services.*
import libra.utils.ServiceError.*
import zio.ZIO

import java.util.UUID

/** Сервис авторов. */
object AuthorService:

  /** Получить всех авторов. */
  def all: ZIO[AuthorRepository, RepositoryError, List[Author]] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.all)
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound("Authors not found"))
        case list => ZIO.succeed(list)
      }

  /** Найти автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора
    */
  def findById(
      id: UUID
  ): ZIO[AuthorRepository, RepositoryError, Author] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.findById(id))
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case None         => ZIO.fail(NotFound(s"Author not found by ID: $id"))
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
      .mapError(e => InternalServerError(e.getMessage))
      .onError(e => ZIO.logError(s"Error:\n${e.prettyPrint}"))
      .flatMap {
        case Nil  => ZIO.fail(NotFound(s"Authors not found by name: $name"))
        case list => ZIO.succeed(list)
      }

  /** Создать автора.
    *
    * @param author
    *   автор
    */
  def create(
      author: Author
  ): ZIO[AuthorRepository, InternalServerError, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.create(author))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$author not created:\n${e.prettyPrint}"))
      _      <- ZIO.logInfo(s"$result created")
    } yield result

  /** Изменить автора.
    *
    * @param author
    *   автор
    */
  def update(
      author: Author
  ): ZIO[AuthorRepository, InternalServerError, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.update(author))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"$author not updated:\n${e.prettyPrint}"))
      _      <- ZIO.logInfo(s"$result updated")
    } yield result

  /** Удалить автора.
    *
    * @param id
    *   уникальный идентификатор автора
    */
  def delete(
      id: UUID
  ): ZIO[AuthorRepository, InternalServerError, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[AuthorRepository](_.delete(id))
        .mapError(e => InternalServerError(e.getMessage))
        .onError(e => ZIO.logError(s"Author ($id) not deleted:\n${e.prettyPrint}"))
      _ <- ZIO.logInfo(s"Author ($id) deleted")
    } yield ()

end AuthorService
