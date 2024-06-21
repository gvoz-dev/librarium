package libra.rest.api.publisher

import libra.entities.Publisher
import libra.repositories.publisher.PublisherRepository
import libra.rest
import libra.services.publisher.PublisherService
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

/** API получения издателей.
  *
  *   - GET /api/v1/publishers
  *   - GET /api/v1/publishers?name={name}
  */
object GetPublishers:

  private val path = "api" / "v1" / "publishers"

  /** Конечная точка API получения издателей. */
  val endpoint: Endpoint[
    Unit,
    Option[String],
    Either[InternalServerError, NotFound],
    List[Publisher],
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.GET / path) ?? Doc.p("Endpoint for querying publishers")
    )
      .query(
        QueryCodec
          .query("name")
          .optional
          ?? Doc.p("Query parameter to search publishers by name")
      )
      .out[List[Publisher]](Doc.p("List of publishers"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения издателей. */
  val route: Route[PublisherRepository, Nothing] =
    endpoint.implement(
      handler((query: Option[String]) =>
        query
          .map(PublisherService.findByName)
          .getOrElse(PublisherService.all)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetPublishers
