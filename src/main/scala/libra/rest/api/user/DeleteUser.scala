package libra.rest.api.user

import libra.config.SecurityConfig
import libra.repositories.user.UserRepository
import libra.rest
import libra.rest.api.authHeader
import libra.services.user.UserService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API удаления пользователя.
  *
  *   - DELETE /api/v1/users/{id}
  */
object DeleteUser:

  private val path = "api" / "v1" / "users" / PathCodec.uuid("userId")

  /** Конечная точка API удаления пользователя. */
  val endpoint: Endpoint[
    UUID,
    (UUID, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.DELETE / path) ?? Doc.p("Deleting user"))
      .header(authHeader)
      .out[Unit](Status.NoContent)
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API удаления пользователя. */
  val route: Route[UserRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((userId: UUID, token: String) =>
        for {
          secret <- Security.secret
          claim  <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
          _      <-
            if JsonWebToken.checkTokenPermissions(claim, userId.toString)
            then UserService.delete(userId).mapError(Right(_))
            else ZIO.fail(Left(Unauthorized("No permission")))
        } yield ()
      )
    )

end DeleteUser
