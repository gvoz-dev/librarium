package libra.rest.api.userbook

import libra.config.SecurityConfig
import libra.repositories.userbook.UserBookRepository
import libra.rest.api.authHeader
import libra.services.userbook.UserBookService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API добавления книги в библиотеку пользователя.
  *
  *   - POST /api/v1/library/{userId}/add/{bookId}
  */
object PostLibraryAdd:

  private val path = "api" / "v1" / "library" / PathCodec.uuid("userId") / "add" / PathCodec.uuid("bookId")

  /** Конечная точка API добавления книги в библиотеку. */
  val endpoint: Endpoint[
    (UUID, UUID),
    (UUID, UUID, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Adding book to library"))
      .header(authHeader)
      .out[Unit]
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API добавления книги в библиотеку. */
  val route: Route[UserBookRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((userId: UUID, bookId: UUID, token: String) =>
        for {
          secret <- Security.secret
          claim  <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
          _      <-
            if JsonWebToken.checkTokenPermissions(claim, userId.toString) then
              UserBookService.addToLibrary(userId, bookId).mapError(Right(_))
            else ZIO.fail(Left(Unauthorized("No permission")))
        } yield ()
      )
    )

end PostLibraryAdd
