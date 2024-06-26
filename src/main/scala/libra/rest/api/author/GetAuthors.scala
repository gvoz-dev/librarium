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

/** API получения авторов.
  *
  *   - GET /api/v1/authors
  *   - GET /api/v1/authors?name={name}
  */
object GetAuthors:

  private val path = "api" / "v1" / "authors"

  /** Конечная точка API получения авторов. */
  val endpoint: Endpoint[
    Unit,
    Option[String],
    Either[InternalServerError, NotFound],
    List[Author],
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying authors"))
      .query(QueryCodec.query("name").optional ?? Doc.p("Query parameter to search authors by name"))
      .out[List[Author]](Doc.p("List of authors"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения авторов. */
  val route: Route[AuthorRepository, Nothing] =
    endpoint.implement(
      handler((query: Option[String]) =>
        query
          .map(AuthorService.findByName)
          .getOrElse(AuthorService.all)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetAuthors
