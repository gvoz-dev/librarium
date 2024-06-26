package libra.rest.api.book

import libra.config.SecurityConfig
import libra.repositories.book.BookRepository
import libra.rest.api.authHeader
import libra.services.book.BookService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API удаления книги.
  *
  *   - DELETE /api/v1/books/{id}
  */
object DeleteBook:

  private val path = "api" / "v1" / "books" / PathCodec.uuid("id")

  /** Конечная точка API удаления книги. */
  val endpoint: Endpoint[
    UUID,
    (UUID, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.DELETE / path) ?? Doc.p("Deleting book"))
      .header(authHeader)
      .out[Unit](Status.NoContent)
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API удаления книги. */
  val route: Route[BookRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((id: UUID, token: String) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> BookService.delete(id).mapError(Right(_))
        } yield result
      )
    )

end DeleteBook
