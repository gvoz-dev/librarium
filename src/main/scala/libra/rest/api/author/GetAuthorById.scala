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

import java.util.UUID

/** API получения автора по идентификатору.
  *
  *   - GET /api/v1/authors/{id}
  */
object GetAuthorById:

  private val path = "api" / "v1" / "authors" / PathCodec.uuid("id")

  /** Конечная точка API получения автора по идентификатору. */
  val endpoint: Endpoint[
    UUID,
    UUID,
    Either[InternalServerError, NotFound],
    Author,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying author by ID"))
      .out[Author](Doc.p("Author"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения автора по идентификатору. */
  val route: Route[AuthorRepository, Nothing] =
    endpoint.implement(
      handler((id: UUID) =>
        AuthorService
          .findById(id)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetAuthorById
