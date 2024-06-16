package itcube.rest.api

import itcube.repositories.user.UserRepository
import itcube.rest.*
import itcube.services.user.UserService
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*
import zio.schema.*

case class Login(email: String, password: String)

object Login:
  given schema: Schema[Login] = DeriveSchema.gen

case class Bearer(token: String)

object Bearer:
  given schema: Schema[Bearer] = DeriveSchema.gen

/** API аутентификации. */
object LoginApi {

  private val path = "api" / "v1" / "login"

  private val loginEndpoint =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Endpoint for login"))
      .in[Login](Doc.p("Login"))
      .examplesIn(
        ("Administrator", Login("admin@example.com", "12345"))
      )
      .out[Bearer](Doc.p("Token"))
      .outError[AuthenticationError](Status.Unauthorized)

  private val loginRoute =
    loginEndpoint.implement(
      handler((login: Login) =>
        UserService
          .findByEmail(login.email)
          .mapError(e => AuthenticationError("Authentication service error"))
          .flatMap {
            case Some(user) =>
              if (login.password == user.password)
                ZIO.succeed(Bearer(JsonWebToken.jwtEncode(user.id.get)))
              else
                ZIO.fail(AuthenticationError("Incorrect password"))
            case None => ZIO.fail(AuthenticationError("Incorrect email"))
          }
      )
    )

  /** Набор конечных точек API аутентификации. */
  val endpoints = Set(
    loginEndpoint
  )

  /** Набор маршрутов API аутентификации. */
  val routes: Routes[UserRepository, Nothing] = Routes(
    loginRoute
  )

}
