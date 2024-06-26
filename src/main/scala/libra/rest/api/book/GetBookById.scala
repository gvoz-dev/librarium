package libra.rest.api.book

import libra.entities.Book
import libra.repositories.book.BookRepository
import libra.services.book.BookService
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API получения книги по идентификатору.
  *
  *   - GET /api/v1/books/{id}
  */
object GetBookById:

  private val path = "api" / "v1" / "books" / PathCodec.uuid("id")

  /** Конечная точка API получения книги по идентификатору. */
  val endpoint: Endpoint[
    UUID,
    UUID,
    Either[InternalServerError, NotFound],
    Book,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying book by ID"))
      .out[Book](Doc.p("Book"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения книги по идентификатору. */
  val route: Route[BookRepository, Nothing] =
    endpoint.implement(
      handler((id: UUID) =>
        BookService
          .findById(id)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetBookById
