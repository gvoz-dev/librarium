package itcube.rest.api

import itcube.entities.Author
import itcube.repositories.author.AuthorRepository
import itcube.rest.*
import itcube.services.*
import itcube.services.author.AuthorService
import itcube.utils.Errors.*
import itcube.utils.JsonWebToken.*
import itcube.utils.Security
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*
import zio.http.endpoint.EndpointMiddleware.*

import java.util.UUID

/** API авторов. */
object AuthorApi:

  private val path: PathCodec[Unit] = "api" / "v1" / "authors"

  // GET api/v1/authors
  // GET api/v1/authors?name={name}
  private val getAuthorsEndpoint =
    Endpoint(
      (RoutePattern.GET / path)
        ?? Doc.p("Route for querying authors")
    )
      .query(
        QueryCodec
          .query("name")
          .optional
          ?? Doc.p("Query parameter to search for authors by name")
      )
      .out[List[Author]](Doc.p("List of authors"))
      .outError[NotFoundError](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InvalidQueryError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val getAuthorsRoute =
    getAuthorsEndpoint.implement(
      handler((query: Option[String]) =>
        val result = query match
          case Some(name) => AuthorService.findByName(name)
          case _          => AuthorService.all
        result.mapError {
          case e: NotFoundError     => Right(e)
          case e: InvalidQueryError => Left(e)
        }
      )
    )

  // GET api/v1/authors/{id}
  private val getAuthorByIdEndpoint =
    Endpoint(
      (RoutePattern.GET / path / PathCodec.string("author_id"))
        ?? Doc.p("Route for querying author by ID")
    )
      .out[Author](Doc.p("Author"))
      .outError[NotFoundError](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InvalidQueryError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val getAuthorByIdRoute =
    getAuthorByIdEndpoint.implement(
      handler((id: String) =>
        AuthorService
          .findById(id)
          .mapError {
            case e: InvalidQueryError => Left(e)
            case e: NotFoundError     => Right(e)
          }
      )
    )

  // POST api/v1/authors
  private val postAuthorEndpoint =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Route for creating author"))
      .header(authHeader ?? Doc.p("JSON Web Token"))
      .in[Author](Doc.p("Author"))
      .examplesIn(
        (
          "Add Harold Abelson",
          ("", Author(scala.None, "Harold Abelson", Some("USA")))
        )
      )
      .out[Author](Doc.p("Created author"))
      .outError[InvalidQueryError](
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
        ?? Doc.p("Route for updating author")
    )
      .in[Author](Doc.p("Author"))
      .examplesIn(
        (
          "Martin Odersky",
          Author(
            Some(UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")),
            "Martin Odersky",
            Some("Switzerland") // Germany -> Switzerland
          )
        )
      )
      .out[Author](Doc.p("Updated author"))
      .outError[InvalidQueryError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val putAuthorRoute =
    putAuthorEndpoint.implement(
      handler((author: Author) => AuthorService.update(author))
    )

  // DELETE api/v1/authors/{id}
  private val deleteAuthorEndpoint =
    Endpoint(
      (RoutePattern.DELETE / path / PathCodec.string("author_id"))
        ?? Doc.p("Route for deleting author")
    )
      .out[Unit]
      .outError[InvalidQueryError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val deleteAuthorRoute =
    deleteAuthorEndpoint.implement(
      handler((id: String) => AuthorService.delete(id))
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
