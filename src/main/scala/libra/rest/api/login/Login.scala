package libra.rest.api.login

import libra.config.SecurityConfig
import libra.repositories.user.UserRepository
import libra.rest
import libra.rest.api.{Credentials, *, given}
import libra.services.user.UserService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

/** API аутентификации пользователя.
  *
  *   - POST /api/v1/login
  */
object Login:

  private val path = "api" / "v1" / "login"

  /** Конечная точка API аутентификации. */
  val endpoint: Endpoint[
    Unit,
    Credentials,
    Unauthorized,
    Token,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Endpoint for login"))
      .in[Credentials](Doc.p("Login credentials"))
      .examplesIn(
        ("Example #1", Credentials("admin@example.com", "12345")),
        ("Example #2", Credentials("roman@example.com", "qwe"))
      )
      .out[Token](Doc.p("JSON Web Token"))
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authentication error")
      )

  /** Маршрут API аутентификации. */
  val route: Route[SecurityConfig & UserRepository, Nothing] =
    endpoint.implement(
      handler((login: Credentials) =>
        UserService
          .findByEmail(login.email)
          .orElseFail(Unauthorized("Invalid email"))
          .flatMap { user =>
            Security
              .validatePassword(login.password, user.password)
              .flatMap {
                case true =>
                  for {
                    userId <- ZIO
                      .fromOption(user.id)
                      .orElseFail(Unauthorized())
                    secret <- Security.secret
                    result <- ZIO
                      .succeed(
                        Token(JsonWebToken.encodeJwt(userId, user.role, secret))
                      )
                  } yield result
                case false =>
                  ZIO.fail(Unauthorized("Invalid password"))
              }
          }
      )
    )

end Login
