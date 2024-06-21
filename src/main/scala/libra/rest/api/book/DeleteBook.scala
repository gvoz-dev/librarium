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

/** API удаления книги.
  *
  *   - DELETE /api/v1/book/{id}
  */
object DeleteBook:

  private val path = "api" / "v1" / "book" / PathCodec.string("id")

  /** Конечная точка API удаления книги. */
  val endpoint: Endpoint[
    String,
    (String, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.DELETE / path) ?? Doc.p("Endpoint for deleting book")
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

  /** Маршрут API удаления книги. */
  val route: Route[BookRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((id: String, token: String) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> BookService.delete(id).mapError(Right(_))
        } yield result
      )
    )

end DeleteBook
