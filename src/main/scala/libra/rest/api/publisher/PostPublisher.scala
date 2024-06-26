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

/** API добавления издателя.
  *
  *   - POST /api/v1/publishers
  */
object PostPublisher:

  private val path = "api" / "v1" / "publishers"

  /** Конечная точка API добавления издателя. */
  val endpoint: Endpoint[
    Unit,
    (String, Publisher),
    Either[Unauthorized, InternalServerError],
    Publisher,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Creating publisher"))
      .header(authHeader)
      .in[Publisher](Doc.p("Publisher"))
      .examplesIn(("Example #1", ("", Publisher(None, "МЦНМО", "Россия"))))
      .out[Publisher](Status.Created, Doc.p("Created publisher"))
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API добавления издателя. */
  val route: Route[PublisherRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, publisher: Publisher) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> PublisherService.create(publisher).mapError(Right(_))
        } yield result
      )
    )

end PostPublisher
