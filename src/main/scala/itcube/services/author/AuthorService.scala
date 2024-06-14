package itcube.services.author

import itcube.entities.Author
import itcube.repositories.author.AuthorRepository
import zio.ZIO

/** Сервис авторов. */
object AuthorService:

  /** Получить всех авторов. */
  def all: ZIO[AuthorRepository, Throwable, List[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.all)

  /** Получить автора по ID.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def findById(
      id: String
  ): ZIO[AuthorRepository, Throwable, Option[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.findById(id))

  /** Получить автора по имени.
    *
    * @param name
    *   имя автора
    */
  def findByName(
      name: String
  ): ZIO[AuthorRepository, Throwable, Option[Author]] =
    ZIO.serviceWithZIO[AuthorRepository](_.findByName(name))

  /** Создать автора.
    *
    * @param author
    *   автор
    */
  def create(
      author: Author
  ): ZIO[AuthorRepository, Throwable, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.create(author))
        .onError(e =>
          ZIO.logError(s"Author `$author` not created:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Author `$result` created")
    } yield result

  /** Изменить автора.
    *
    * @param author
    *   автор
    */
  def update(
      author: Author
  ): ZIO[AuthorRepository, Throwable, Author] =
    for {
      result <- ZIO
        .serviceWithZIO[AuthorRepository](_.update(author))
        .onError(e =>
          ZIO.logError(s"Author `$author` not updated:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Author `$result` updated")
    } yield result

  /** Удалить автора.
    *
    * @param id
    *   уникальный идентификатор автора (строка UUID).
    */
  def delete(
      id: String
  ): ZIO[AuthorRepository, Throwable, Unit] =
    for {
      _ <- ZIO
        .serviceWithZIO[AuthorRepository](_.delete(id))
        .onError(e =>
          ZIO.logError(s"Author `$id` not deleted:\n${e.prettyPrint}")
        )
      _ <- ZIO.logInfo(s"Author `$id` deleted")
    } yield ()

end AuthorService
