package itcube.rest.api

import itcube.repositories.user.UserRepository
import itcube.rest.{Credentials, *}
import itcube.services.user.UserService
import itcube.utils.*
import itcube.utils.Errors.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

/** API аутентификации пользователя. */
object LoginApi:

  private val path: PathCodec[Unit] = "api" / "v1" / "login"

  // POST api/v1/login
  private val loginEndpoint =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Endpoint for login"))
      .in[Credentials](Doc.p("Login credentials"))
      .examplesIn(
        ("Administrator", Credentials("admin@example.com", "12345")),
        ("User", Credentials("roman@example.com", "qwe"))
      )
      .out[Token](Doc.p("JSON Web Token"))
      .outError[AuthenticationError](Status.Unauthorized)

  private val loginRoute =
    loginEndpoint.implement(
      handler((login: Credentials) =>
        UserService
          .findByEmail(login.email)
          .orElseFail(AuthenticationError("Authentication service error"))
          .flatMap {
            // Пользователь с данной электронной почтой существует
            case Some(user) =>
              Security
                .validatePassword(login.password, user.password)
                .flatMap {
                  // Пароль является валидным
                  case true =>
                    for {
                      userId <- ZIO
                        .fromOption(user.id)
                        .orElseFail(
                          AuthenticationError("Authentication service error")
                        )
                      secret <- Security.secret
                      result <- ZIO
                        .succeed(Token(JsonWebToken.encodeJwt(userId, secret)))
                    } yield result
                  // Пароль валидным не является
                  case false =>
                    ZIO.fail(AuthenticationError("Incorrect password"))
                }
            // Не существует пользователя с данной электронной почтой
            case None => ZIO.fail(AuthenticationError("Incorrect email"))
          }
      )
    )

  /** Набор конечных точек API аутентификации. */
  val endpoints = Set(
    loginEndpoint
  )

  /** Набор маршрутов API аутентификации. */
  val routes = Routes(
    loginRoute
  )

end LoginApi
