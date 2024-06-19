package libra.rest.api

import libra.config.SecurityConfig
import libra.repositories.user.UserRepository
import libra.rest.{Credentials, *, given}
import libra.services.user.UserService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*
import zio.http.endpoint.EndpointMiddleware.None

/** API аутентификации пользователя. */
object LoginApi:

  private val path = "api" / "v1" / "login"

  // POST api/v1/login
  private val loginEndpoint =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Endpoint for login"))
      .in[Credentials](Doc.p("Login credentials"))
      .examplesIn(
        ("Example #1", Credentials("admin@example.com", "12345")),
        ("Example #2", Credentials("roman@example.com", "qwe"))
      )
      .out[Token](Doc.p("JSON Web Token"))
      .outError[AuthenticationError](
        Status.Unauthorized,
        Doc.p("Authentication error")
      )

  private val loginRoute =
    loginEndpoint.implement(
      handler((login: Credentials) =>
        UserService
          .findByEmail(login.email)
          .orElseFail(AuthenticationError())
          .flatMap { user =>
            Security
              .validatePassword(login.password, user.password)
              .flatMap {
                case true =>
                  for {
                    userId <- ZIO
                      .fromOption(user.id)
                      .orElseFail(AuthenticationError())
                    secret <- Security.secret
                    result <- ZIO
                      .succeed(
                        Token(JsonWebToken.encodeJwt(userId, user.role, secret))
                      )
                  } yield result
                case false =>
                  ZIO.fail(AuthenticationError("Incorrect password"))
              }
          }
      )
    )

  /** Набор конечных точек API аутентификации. */
  val endpoints = Set(loginEndpoint)

  /** Набор маршрутов API аутентификации. */
  val routes = Routes(loginRoute)

end LoginApi
