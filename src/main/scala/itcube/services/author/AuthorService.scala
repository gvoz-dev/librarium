package itcube.services.author

import itcube.entities.Author
import itcube.repositories.author.AuthorRepository
import itcube.services.*
import itcube.utils.Errors.*
import zio.ZIO

/** Сервис авторов. */
object AuthorService:

  /** Получить всех авторов. */
  def all: ZIO[AuthorRepository, ServiceError, List[Author]] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.all)
      .mapError(e => InvalidQueryError(e.getMessage))
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
  ): ZIO[AuthorRepository, ServiceError, Author] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.findById(id))
      .mapError(e => InvalidQueryError(e.getMessage))
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
  ): ZIO[AuthorRepository, ServiceError, List[Author]] =
    ZIO
      .serviceWithZIO[AuthorRepository](_.findByName(name))
      .mapError(e => InvalidQueryError(e.getMessage))
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
  ): ZIO[AuthorRepository, InvalidQueryError, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.create(author))
        .mapError(e => InvalidQueryError(e.getMessage))
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
  ): ZIO[AuthorRepository, InvalidQueryError, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.update(author))
        .mapError(e => InvalidQueryError(e.getMessage))
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
  ): ZIO[AuthorRepository, InvalidQueryError, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[AuthorRepository](_.delete(id))
        .mapError(e => InvalidQueryError(e.getMessage))
        .onError(e =>
          ZIO.logError(s"Author ($id) not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Author ($id) deleted")
    } yield ()

end AuthorService
