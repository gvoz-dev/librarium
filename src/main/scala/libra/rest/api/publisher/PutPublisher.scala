package libra.rest.api.publisher

import libra.config.SecurityConfig
import libra.entities.Publisher
import libra.repositories.publisher.PublisherRepository
import libra.rest
import libra.rest.api.authHeader
import libra.services.publisher.PublisherService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API изменения издателя.
  *
  *   - PUT /api/v1/publishers
  */
object PutPublisher:

  private val path = "api" / "v1" / "publishers"

  /** Конечная точка API изменения издателя. */
  val endpoint: Endpoint[
    Unit,
    (String, Publisher),
    Either[Unauthorized, InternalServerError],
    Publisher,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.PUT / path) ?? Doc.p("Endpoint for updating publisher")
    )
      .header(authHeader)
      .in[Publisher](Doc.p("Publisher"))
      .examplesIn(
        (
          "Example #1",
          (
            "",
            Publisher(
              Some(UUID.fromString("4c007df8-4c12-435b-9c1d-082e204db21e")),
              "Наука",
              "USSR"
            )
          )
        )
      )
      .out[Publisher](Doc.p("Updated publisher"))
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )

  /** Маршрут API изменения издателя. */
  val route: Route[PublisherRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, publisher: Publisher) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> PublisherService.update(publisher).mapError(Right(_))
        } yield result
      )
    )

end PutPublisher
