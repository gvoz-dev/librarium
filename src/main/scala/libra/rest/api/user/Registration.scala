package libra.rest.api.user

import libra.entities.User
import libra.repositories.user.UserRepository
import libra.rest
import libra.services.user.UserService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

/** API регистрации пользователей.
  *
  *   - POST /api/v1/registration
  */
object Registration:

  private val path = "api" / "v1" / "registration"

  /** Конечная точка API регистрации. */
  val endpoint: Endpoint[
    Unit,
    User,
    Either[BadRequest, InternalServerError],
    User,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("User registration"))
      .in[User](Doc.p("User"))
      .examplesIn(
        (
          "Example #1",
          User(None, "Houdini", "houdini@example.com", "AllToScala")
        )
      )
      .out[User](Status.Created, Doc.p("Registered user"))
      .outError[InternalServerError](Status.InternalServerError)
      .outError[BadRequest](Status.BadRequest)

  /** Маршрут API регистрации. */
  val route: Route[UserRepository, Nothing] =
    endpoint.implement(
      handler((user: User) =>
        for {
          hash   <- Security.hashPassword(user.password)
          result <- UserService
            .create(user.copy(password = hash))
            .orElseFail(Right(InternalServerError("Registration failed")))
        } yield result
      )
    )

end Registration
