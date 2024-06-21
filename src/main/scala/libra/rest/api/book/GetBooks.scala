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

/** API получения книг.
  *
  *   - GET /api/v1/books
  *   - GET /api/v1/books?title={title}
  */
object GetBooks:

  private val path = "api" / "v1" / "books"

  /** Конечная точка API получения книг. */
  val endpoint: Endpoint[
    Unit,
    Option[String],
    Either[InternalServerError, NotFound],
    List[Book],
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.GET / path) ?? Doc.p("Endpoint for querying books")
    )
      .query(
        QueryCodec
          .query("title")
          .optional
          ?? Doc.p("Query parameter to search books by title")
      )
      .out[List[Book]](Doc.p("List of books"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения книг. */
  val route: Route[BookRepository, Nothing] =
    endpoint.implement(
      handler((query: Option[String]) =>
        query
          .map(BookService.findByTitle)
          .getOrElse(BookService.all)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetBooks
