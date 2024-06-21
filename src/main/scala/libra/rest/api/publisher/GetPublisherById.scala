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

/** API получения издателя по идентификатору.
  *
  *   - GET /api/v1/publishers/{id}
  */
object GetPublisherById:

  private val path = "api" / "v1" / "publishers" / PathCodec.string("id")

  /** Конечная точка API получения издателя по идентификатору. */
  val endpoint: Endpoint[
    String,
    String,
    Either[InternalServerError, NotFound],
    Publisher,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.GET / path)
        ?? Doc.p("Endpoint for querying publisher by ID")
    )
      .out[Publisher](Doc.p("Publisher"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения издателя по идентификатору. */
  val route: Route[PublisherRepository, Nothing] =
    endpoint.implement(
      handler((id: String) =>
        PublisherService
          .findById(id)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetPublisherById
