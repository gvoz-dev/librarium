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

/** API получения пользователей.
  *
  *   - GET /api/v1/users
  *   - GET /api/v1/users?name={name}
  */
object GetUsers:

  private val path = "api" / "v1" / "users"

  /** Конечная точка API получения пользователей. */
  val endpoint: Endpoint[
    Unit,
    Option[String],
    Either[InternalServerError, NotFound],
    List[User],
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Endpoint for querying users"))
      .query(
        QueryCodec
          .query("name")
          .optional
          ?? Doc.p("Query parameter to search users by name")
      )
      .out[List[User]](Doc.p("List of users"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Users not found")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения пользователей. */
  val route: Route[UserRepository, Nothing] =
    endpoint.implement(
      handler((query: Option[String]) =>
        query
          .map(UserService.findByName)
          .getOrElse(UserService.all)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetUsers
