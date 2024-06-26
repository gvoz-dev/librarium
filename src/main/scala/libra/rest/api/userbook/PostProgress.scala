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

/** API установки прогресса прочитанного.
  *
  *   - POST /api/v1/progress/{bookId}/{userId}
  */
object PostProgress:

  private val path = "api" / "v1" / "progress"
    / PathCodec.uuid("bookId") / PathCodec.uuid("userId")

  /** Конечная точка API установки прогресса прочитанного. */
  val endpoint: Endpoint[
    (UUID, UUID),
    (UUID, UUID, String, Progress),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Setting reading progress"))
      .header(authHeader)
      .in[Progress](Doc.p("Progress"))
      .examplesIn(
        (
          "Example #1",
          (
            UUID.fromString("b43e5b87-a042-461b-8728-653eddced002"),
            UUID.fromString("ea962bb3-8f66-4256-bea5-8851c8f37dfb"),
            "",
            Progress(42.0f)
          )
        )
      )
      .out[Unit]
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API установки прогресса прочитанного. */
  val route: Route[UserBookRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((bookId: UUID, userId: UUID, token: String, progress: Progress) =>
        for {
          secret <- Security.secret
          claim  <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
          _      <-
            if JsonWebToken.checkTokenPermissions(claim, userId.toString) then
              UserBookService
                .setProgress(userId, bookId, progress.value)
                .mapError(Right(_))
            else ZIO.fail(Left(Unauthorized("No permission")))
        } yield ()
      )
    )

end PostProgress
