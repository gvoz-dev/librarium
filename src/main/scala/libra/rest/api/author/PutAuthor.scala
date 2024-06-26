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

import java.util.UUID

/** API изменения автора.
  *
  *   - PUT /api/v1/authors
  */
object PutAuthor:

  private val path = "api" / "v1" / "authors"

  /** Конечная точка API изменения автора. */
  val endpoint: Endpoint[
    Unit,
    (String, Author),
    Either[Unauthorized, InternalServerError],
    Author,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.PUT / path) ?? Doc.p("Updating author"))
      .header(authHeader)
      .in[Author](Doc.p("Author"))
      .examplesIn(
        (
          "Example #1",
          (
            "",
            Author(
              Some(UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")),
              "Martin Odersky",
              Some("Switzerland")
            )
          )
        )
      )
      .out[Author](Doc.p("Updated author"))
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API изменения автора. */
  val route: Route[AuthorRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, author: Author) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> AuthorService.update(author).mapError(Right(_))
        } yield result
      )
    )

end PutAuthor
