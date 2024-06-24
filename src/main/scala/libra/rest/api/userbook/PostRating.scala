package libra.rest.api.userbook

import libra.config.SecurityConfig
import libra.repositories.userbook.UserBookRepository
import libra.rest.api.{*, given}
import libra.services.userbook.UserBookService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API установки пользовательского рейтинга.
  *
  *   - POST /api/v1/rating/{bookId}/{userId}
  */
object PostRating:

  private val path = "api" / "v1" / "rating"
    / PathCodec.uuid("bookId") / PathCodec.uuid("userId")

  /** Конечная точка API установки пользовательского рейтинга. */
  val endpoint: Endpoint[
    (UUID, UUID),
    (UUID, UUID, String, Rating),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.POST / path)
        ?? Doc.p("Endpoint for setting rating")
    )
      .header(authHeader)
      .in[Rating](Doc.p("Rating"))
      .examplesIn(
        (
          "Example #1",
          (
            UUID.fromString("b43e5b87-a042-461b-8728-653eddced002"),
            UUID.fromString("ea962bb3-8f66-4256-bea5-8851c8f37dfb"),
            "",
            Rating(4)
          )
        )
      )
      .out[Unit]
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )

  /** Маршрут API установки пользовательского рейтинга. */
  val route: Route[UserBookRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((bookId: UUID, userId: UUID, token: String, rating: Rating) =>
        for {
          secret <- Security.secret
          claim <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
          _ <-
            if JsonWebToken.checkTokenPermissions(claim, userId.toString) then
              UserBookService
                .setRating(userId, bookId, rating.value)
                .mapError(Right(_))
            else ZIO.fail(Left(Unauthorized("No permission")))
        } yield ()
      )
    )

end PostRating
