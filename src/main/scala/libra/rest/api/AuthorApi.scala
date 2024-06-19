package libra.rest.api

import libra.config.SecurityConfig
import libra.entities.Author
import libra.repositories.author.AuthorRepository
import libra.rest.*
import libra.services.*
import libra.services.author.AuthorService
import libra.utils.*
import libra.utils.JsonWebToken.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API авторов. */
object AuthorApi:

  private val path: PathCodec[Unit] = "api" / "v1" / "authors"

  // GET api/v1/authors
  // GET api/v1/authors?name={name}
  private val getAuthorsEndpoint =
    Endpoint(
      (RoutePattern.GET / path)
        ?? Doc.p("Endpoint for querying authors")
    )
      .query(
        QueryCodec
          .query("name")
          .optional
          ?? Doc.p("Query parameter to search authors by name")
      )
      .out[List[Author]](Doc.p("List of authors"))
      .outError[NotFoundError](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val getAuthorsRoute =
    getAuthorsEndpoint.implement(
      handler((query: Option[String]) =>
        query
          .map(AuthorService.findByName)
          .getOrElse(AuthorService.all)
          .mapError {
            case err: DatabaseError => Left(err)
            case err: NotFoundError => Right(err)
          }
      )
    )

  // GET api/v1/authors/{id}
  private val getAuthorByIdEndpoint =
    Endpoint(
      (RoutePattern.GET / path / PathCodec.string("authorId"))
        ?? Doc.p("Endpoint for querying author by ID")
    )
      .out[Author](Doc.p("Author"))
      .outError[NotFoundError](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val getAuthorByIdRoute =
    getAuthorByIdEndpoint.implement(
      handler((authorId: String) =>
        AuthorService
          .findById(authorId)
          .mapError {
            case err: DatabaseError => Left(err)
            case err: NotFoundError => Right(err)
          }
      )
    )

  // POST api/v1/authors
  private val postAuthorEndpoint =
    Endpoint(
      (RoutePattern.POST / path)
        ?? Doc.p("Endpoint for creating author")
    )
      .header(authHeader)
      .in[Author](Doc.p("Author"))
      .examplesIn(
        (
          "Example #1",
          ("", Author(scala.None, "Harold Abelson", Some("USA")))
        )
      )
      .out[Author](Doc.p("Created author"))
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[AuthenticationError](
        Status.Unauthorized,
        Doc.p("Invalid token")
      )

  private val postAuthorRoute =
    postAuthorEndpoint.implement(
      handler((token: String, author: Author) =>
        for {
          secret <- Security.secret
          result <- validateJwt(token, secret).mapError(Left(_))
            *> AuthorService.create(author).mapError(Right(_))
        } yield result
      )
    )

  // PUT api/v1/authors
  private val putAuthorEndpoint =
    Endpoint(
      (RoutePattern.PUT / path)
        ?? Doc.p("Endpoint for updating author")
    )
      .header(authHeader)
      .in[Author](Doc.p("Author"))
      .examplesIn(
        (
          "Example #1",
          (
            "",
            Author(
              Some(UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")),
              "Martin Odersky",
              Some("Switzerland")
            )
          )
        )
      )
      .out[Author](Doc.p("Updated author"))
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[AuthenticationError](
        Status.Unauthorized,
        Doc.p("Invalid token")
      )

  private val putAuthorRoute =
    putAuthorEndpoint.implement(
      handler((token: String, author: Author) =>
        for {
          secret <- Security.secret
          result <- validateJwt(token, secret).mapError(Left(_))
            *> AuthorService.update(author).mapError(Right(_))
        } yield result
      )
    )

  // DELETE api/v1/authors/{id}
  private val deleteAuthorEndpoint =
    Endpoint(
      (RoutePattern.DELETE / path / PathCodec.string("authorId"))
        ?? Doc.p("Endpoint for deleting author")
    )
      .header(authHeader)
      .out[Unit]
      .outError[DatabaseError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[AuthenticationError](
        Status.Unauthorized,
        Doc.p("Invalid token")
      )

  private val deleteAuthorRoute =
    deleteAuthorEndpoint.implement(
      handler((authorId: String, token: String) =>
        for {
          secret <- Security.secret
          result <- validateJwt(token, secret).mapError(Left(_))
            *> AuthorService.delete(authorId).mapError(Right(_))
        } yield result
      )
    )

  /** Набор конечных точек API авторов. */
  val endpoints = Set(
    getAuthorsEndpoint,
    getAuthorByIdEndpoint,
    postAuthorEndpoint,
    putAuthorEndpoint,
    deleteAuthorEndpoint
  )

  /** Набор маршрутов API авторов. */
  val routes = Routes(
    getAuthorsRoute,
    getAuthorByIdRoute,
    postAuthorRoute,
    putAuthorRoute,
    deleteAuthorRoute
  )

end AuthorApi
