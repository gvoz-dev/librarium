package libra.rest.api.user

import libra.config.SecurityConfig
import libra.entities.User
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

/** API изменения пользователя.
  *
  *   - PUT /api/v1/users
  */
object PutUser:

  private val path = "api" / "v1" / "users"

  /** Конечная точка API изменения пользователя. */
  val endpoint: Endpoint[
    Unit,
    (String, User),
    Either[BadRequest, Either[Unauthorized, InternalServerError]],
    User,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.PUT / path) ?? Doc.p("Endpoint for updating user"))
      .header(authHeader)
      .in[User](Doc.p("User"))
      .examplesIn(
        (
          "Example #1",
          (
            "",
            User(
              Some(UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c")),
              "Roman",
              "roma@example.com",
              "test"
            )
          )
        )
      )
      .out[User](Doc.p("Updated user"))
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )
      .outError[BadRequest](
        Status.BadRequest,
        Doc.p("Invalid user data")
      )

  /** Маршрут API изменения пользователя. */
  val route: Route[UserRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, user: User) =>
        for {
          userId <- ZIO
            .fromOption(user.id)
            .mapBoth(_ => Left(BadRequest("No user id")), _.toString)
          _ <- ZIO.ifZIO(Security.validateEmail(user.email))(
            onFalse = ZIO.fail(Left(BadRequest("Invalid email"))),
            onTrue = ZIO.unit
          )
          secret <- Security.secret
          claim <- JsonWebToken
            .validateJwt(token, secret)
            .mapError(err => Right(Left(err)))
          hash <- Security.hashPassword(user.password)
          result <-
            if JsonWebToken.checkTokenPermissions(claim, userId) then
              UserService
                .update(user.copy(password = hash))
                .mapError(err => Right(Right(err)))
            else ZIO.fail(Right(Left(Unauthorized("No permission"))))
        } yield result
      )
    )

end PutUser
