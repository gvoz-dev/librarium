package libra.rest.api.author

import libra.config.SecurityConfig
import libra.entities.Author
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

/** API добавления автора.
  *
  *   - POST /api/v1/authors
  */
object PostAuthor:

  private val path = "api" / "v1" / "authors"

  /** Конечная точка API добавления автора. */
  val endpoint: Endpoint[
    Unit,
    (String, Author),
    Either[Unauthorized, InternalServerError],
    Author,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Creating author"))
      .header(authHeader)
      .in[Author](Doc.p("Author"))
      .examplesIn(("Example #1", ("", Author(None, "Harold Abelson", Some("USA")))))
      .out[Author](Status.Created, Doc.p("Created author"))
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API добавления автора. */
  val route: Route[AuthorRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, author: Author) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> AuthorService.create(author).mapError(Right(_))
        } yield result
      )
    )

end PostAuthor
