package itcube.rest.api

import itcube.entities.Author
import itcube.repositories.author.AuthorRepository
import itcube.services.*
import itcube.services.author.AuthorService
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*
import zio.http.endpoint.EndpointMiddleware.*

import java.util.UUID

/** API авторов. */
object AuthorApi:

  private val path = "api" / "v1" / "authors"

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
      .outError[QueryError](
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
          case e: NotFoundError => Right(e)
          case e: QueryError    => Left(e)
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
      .outError[QueryError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val getAuthorByIdRoute =
    getAuthorByIdEndpoint.implement(
      handler((id: String) =>
        AuthorService
          .findById(id)
          .mapError {
            case e: QueryError    => Left(e)
            case e: NotFoundError => Right(e)
          }
      )
    )

  // POST api/v1/authors
  private val postAuthorEndpoint =
    Endpoint(
      (RoutePattern.POST / path)
        ?? Doc.p("Route for creating author")
    )
      .in[Author](Doc.p("Author"))
      .examplesIn(
        ("Harold Abelson", Author(scala.None, "Harold Abelson", Some("USA"))),
        ("Gerald Sussman", Author(scala.None, "Gerald Sussman", Some("USA")))
      )
      .out[Author](Doc.p("Created author"))
      .outError[QueryError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val postAuthorRoute =
    postAuthorEndpoint.implement(
      handler((author: Author) => AuthorService.create(author))
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
      .outError[QueryError](
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
      .outError[QueryError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  private val deleteAuthorRoute =
    deleteAuthorEndpoint.implement(
      handler((id: String) => AuthorService.delete(id))
    )

  /** Набор конечных точек API авторов. */
  val endpoints: Set[Endpoint[
    ? >: Unit & String,
    ? >: Option[String] & String & Author <: Serializable,
    ? >: Either[QueryError, NotFoundError] & QueryError <: Product,
    ? >: List[Author] & Author & Unit,
    None
  ]] = Set(
    getAuthorsEndpoint,
    getAuthorByIdEndpoint,
    postAuthorEndpoint,
    putAuthorEndpoint,
    deleteAuthorEndpoint
  )

  /** Набор маршрутов API авторов. */
  val routes: Routes[AuthorRepository, Response] = Routes(
    getAuthorsRoute,
    getAuthorByIdRoute,
    postAuthorRoute,
    putAuthorRoute,
    deleteAuthorRoute
  )

end AuthorApi
