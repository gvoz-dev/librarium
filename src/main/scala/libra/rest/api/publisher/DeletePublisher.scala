package libra.rest.api.publisher

import libra.config.SecurityConfig
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

/** API удаления издателя.
  *
  *   - DELETE /api/v1/publishers/{id}
  */
object DeletePublisher:

  private val path = "api" / "v1" / "publishers" / PathCodec.string("id")

  /** Конечная точка API удаления издателя. */
  val endpoint: Endpoint[
    String,
    (String, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.DELETE / path) ?? Doc.p("Endpoint for deleting publisher")
    )
      .header(authHeader)
      .out[Unit]
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )

  /** Маршрут API удаления издателя. */
  val route: Route[PublisherRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((id: String, token: String) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> PublisherService.delete(id).mapError(Right(_))
        } yield result
      )
    )

end DeletePublisher
