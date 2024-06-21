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

/** API получения книги по идентификатору.
  *
  *   - GET /api/v1/books/{id}
  */
object GetBookById:

  private val path = "api" / "v1" / "books" / PathCodec.string("id")

  /** Конечная точка API получения книги по идентификатору. */
  val endpoint: Endpoint[
    String,
    String,
    Either[InternalServerError, NotFound],
    Book,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.GET / path)
        ?? Doc.p("Endpoint for querying book by ID")
    )
      .out[Book](Doc.p("Book"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения книги по идентификатору. */
  val route: Route[BookRepository, Nothing] =
    endpoint.implement(
      handler((id: String) =>
        BookService
          .findById(id)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetBookById
