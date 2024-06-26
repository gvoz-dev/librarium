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

import java.util.UUID

/** API удаления издателя.
  *
  *   - DELETE /api/v1/publishers/{id}
  */
object DeletePublisher:

  private val path = "api" / "v1" / "publishers" / PathCodec.uuid("id")

  /** Конечная точка API удаления издателя. */
  val endpoint: Endpoint[
    UUID,
    (UUID, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.DELETE / path) ?? Doc.p("Deleting publisher"))
      .header(authHeader)
      .out[Unit](Status.NoContent)
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API удаления издателя. */
  val route: Route[PublisherRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((id: UUID, token: String) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> PublisherService.delete(id).mapError(Right(_))
        } yield result
      )
    )

end DeletePublisher
