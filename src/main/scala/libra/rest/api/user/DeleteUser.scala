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

/** API удаления пользователя.
  *
  *   - DELETE /api/v1/users/{id}
  */
object DeleteUser:

  private val path = "api" / "v1" / "users" / PathCodec.string("userId")

  /** Конечная точка API удаления пользователя. */
  val endpoint: Endpoint[
    String,
    (String, String),
    Either[Unauthorized, InternalServerError],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.DELETE / path) ?? Doc.p("Endpoint for deleting user")
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

  /** Маршрут API удаления пользователя. */
  val route: Route[UserRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((userId: String, token: String) =>
        for {
          secret <- Security.secret
          claim <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
          _ <-
            if JsonWebToken.checkTokenPermissions(claim, userId) then
              UserService.delete(userId).mapError(Right(_))
            else ZIO.fail(Left(Unauthorized("No permission")))
        } yield ()
      )
    )

end DeleteUser
