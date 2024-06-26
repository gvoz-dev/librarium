package libra.rest.api.author

import libra.config.SecurityConfig
import libra.repositories.author.AuthorRepository
import libra.rest
import libra.rest.api.authHeader
import libra.services.author.AuthorService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API удаления автора.
  *
  *   - DELETE /api/v1/authors/{id}
  */
object DeleteAuthor:

  private val path = "api" / "v1" / "authors" / PathCodec.uuid("id")

  /** Конечная точка API удаления автора. */
  val endpoint: Endpoint[
    UUID,
    (UUID, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.DELETE / path) ?? Doc.p("Delete author"))
      .header(authHeader)
      .out[Unit](Status.NoContent)
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API удаления автора. */
  val route: Route[AuthorRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((id: UUID, token: String) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> AuthorService.delete(id).mapError(Right(_))
        } yield result
      )
    )

end DeleteAuthor
