package libra.rest.api.author

import libra.entities.Author
import libra.repositories.author.AuthorRepository
import libra.rest
import libra.services.author.AuthorService
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

/** API получения автора по идентификатору.
  *
  *   - GET /api/v1/authors/{id}
  */
object GetAuthorById:

  private val path = "api" / "v1" / "authors" / PathCodec.string("id")

  /** Конечная точка API получения автора по идентификатору. */
  val endpoint: Endpoint[
    String,
    String,
    Either[InternalServerError, NotFound],
    Author,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.GET / path) ?? Doc.p("Endpoint for querying author by ID")
    )
      .out[Author](Doc.p("Author"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения автора по идентификатору. */
  val route: Route[AuthorRepository, Nothing] =
    endpoint.implement(
      handler((id: String) =>
        AuthorService
          .findById(id)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetAuthorById
