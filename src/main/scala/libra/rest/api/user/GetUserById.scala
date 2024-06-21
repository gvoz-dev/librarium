package libra.rest.api.user

import libra.entities.User
import libra.repositories.user.UserRepository
import libra.rest
import libra.services.user.UserService
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

/** API получения пользователя по идентификатору.
  *
  *   - GET /api/v1/users/{id}
  */
object GetUserById:

  private val path = "api" / "v1" / "users" / PathCodec.string("userId")

  /** Конечная точка API получения пользователя по идентификатору. */
  val endpoint: Endpoint[
    String,
    String,
    Either[InternalServerError, NotFound],
    User,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.GET / path) ?? Doc.p("Endpoint for querying user by ID")
    )
      .out[User](Doc.p("User"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("User not found")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения пользователя по идентификатору. */
  val route: Route[UserRepository, Nothing] =
    endpoint.implement(
      handler((userId: String) =>
        UserService
          .findById(userId)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetUserById
