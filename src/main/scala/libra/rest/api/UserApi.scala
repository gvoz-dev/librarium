package libra.rest.api

import libra.entities.User
import libra.rest.*
import libra.services.user.UserService
import libra.utils.JsonWebToken.*
import libra.utils.Security
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API пользователей. */
object UserApi:

  // POST /api/v1/registration
  private val registrationEndpoint =
    Endpoint(
      (RoutePattern.POST / "api" / "v1" / "registration")
        ?? Doc.p("Endpoint for user registration")
    )
      .in[User](Doc.p("User"))
      .examplesIn(
        (
          "Example #1",
          User(None, "Houdini", "houdini@example.com", "AllToScala")
        )
      )
      .out[User](Doc.p("Registered user"))
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Registration failed")
      )

  private val registrationRoute =
    registrationEndpoint.implement(
      handler((user: User) =>
        for {
          hash <- Security.hashPassword(user.password)
          result <- UserService
            .create(user.copy(password = hash))
            .mapError(e => e.copy(message = "Registration failed"))
        } yield result
      )
    )

  private val path = "api" / "v1" / "users"

  // GET /api/v1/users
  // GET /api/v1/users?name={name}
  private val getUsersEndpoint =
    Endpoint(
      (RoutePattern.GET / path)
        ?? Doc.p("Endpoint for querying users")
    )
      .query(
        QueryCodec
          .query("name")
          .optional
          ?? Doc.p("Query parameter to search users by name")
      )
      .out[List[User]](Doc.p("List of users"))
      .outError[NotFoundError](
        Status.NotFound,
        Doc.p("Users not found")
      )
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val getUsersRoute =
    getUsersEndpoint.implement(
      handler((query: Option[String]) =>
        query.map(UserService.findByName).getOrElse(UserService.all).mapError {
          case err: DatabaseError => Left(err)
          case err: NotFoundError => Right(err)
        }
      )
    )

  // GET /api/v1/users/{id}
  private val getUserByIdEndpoint =
    Endpoint(
      (RoutePattern.GET / path / PathCodec.string("userId"))
        ?? Doc.p("Endpoint for querying user by ID")
    )
      .out[User](Doc.p("User"))
      .outError[NotFoundError](
        Status.NotFound,
        Doc.p("User not found")
      )
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val getUserByIdRoute =
    getUserByIdEndpoint.implement(
      handler((userId: String) =>
        UserService
          .findById(userId)
          .mapError {
            case err: DatabaseError => Left(err)
            case err: NotFoundError => Right(err)
          }
      )
    )

  // PUT /api/v1/users
  private val putUserEndpoint =
    Endpoint(
      (RoutePattern.PUT / path)
        ?? Doc.p("Endpoint for updating user")
    )
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
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[AuthenticationError](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )

  private val putUserRoute =
    putUserEndpoint.implement(
      handler((token: String, user: User) =>
        for {
          secret <- Security.secret
          claim <- validateJwt(token, secret).mapError(Left(_))
          userId <- ZIO
            .fromOption(user.id)
            .mapBoth(_ => Left(AuthenticationError()), _.toString)
          hash <- Security.hashPassword(user.password)
          result <-
            if checkTokenPermissions(claim, userId) then
              UserService.update(user.copy(password = hash)).mapError(Right(_))
            else ZIO.fail(Left(AuthenticationError("No permission")))
        } yield result
      )
    )

  // DELETE /api/v1/users/{id}
  private val deleteUserEndpoint =
    Endpoint(
      (RoutePattern.DELETE / path / PathCodec.string("userId"))
        ?? Doc.p("Endpoint for deleting user")
    )
      .header(authHeader)
      .out[Unit]
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[AuthenticationError](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )

  private val deleteUserRoute =
    deleteUserEndpoint.implement(
      handler((userId: String, token: String) =>
        for {
          secret <- Security.secret
          claim <- validateJwt(token, secret).mapError(Left(_))
          _ <-
            if checkTokenPermissions(claim, userId) then
              UserService.delete(userId).mapError(Right(_))
            else ZIO.fail(Left(AuthenticationError("No permission")))
        } yield ()
      )
    )

  /** Набор конечных точек API пользователей. */
  val endpoints = Set(
    registrationEndpoint,
    getUsersEndpoint,
    getUserByIdEndpoint,
    putUserEndpoint,
    deleteUserEndpoint
  )

  /** Набор маршрутов API пользователей. */
  val routes = Routes(
    registrationRoute,
    getUsersRoute,
    getUserByIdRoute,
    putUserRoute,
    deleteUserRoute
  )

end UserApi
