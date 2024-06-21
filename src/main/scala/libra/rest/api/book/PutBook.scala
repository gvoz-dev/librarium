package libra.rest.api.book

import libra.config.SecurityConfig
import libra.entities.*
import libra.repositories.book.BookRepository
import libra.rest.api.authHeader
import libra.services.book.BookService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API изменения книги.
  *
  *   - PUT /api/v1/books
  */
object PutBook:

  private val path = "api" / "v1" / "books"

  /** Конечная точка API изменения книги. */
  val endpoint: Endpoint[
    Unit,
    (String, Book),
    Either[Unauthorized, InternalServerError],
    Book,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.PUT / path) ?? Doc.p("Endpoint for updating book")
    )
      .header(authHeader)
      .in[Book](Doc.p("Book"))
      .examplesIn(
        (
          "Example #1",
          (
            "",
            Book(
              Some(UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")),
              "Теория вероятностей и математическая статистика",
              None,
              None,
              None,
              Some(1986),
              Some(535),
              None,
              Some("Сборник статей Колмогорова"),
              Some("RU"),
              Some("Math"),
              Some(Publisher(None, "Наука", "СССР")),
              Some(Author(None, "Колмогоров А.Н.", Some("СССР")))
            )
          )
        )
      )
      .out[Book](Doc.p("Updated book"))
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )

  /** Маршрут API изменения книги. */
  val route: Route[BookRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, book: Book) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> BookService.update(book).mapError(Right(_))
        } yield result
      )
    )

end PutBook
