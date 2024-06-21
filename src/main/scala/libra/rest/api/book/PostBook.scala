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

/** API добавления книги.
  *
  *   - POST /api/v1/books
  */
object PostBook:

  private val path = "api" / "v1" / "books"

  /** Конечная точка API добавления книги. */
  val endpoint: Endpoint[
    Unit,
    (String, Book),
    Either[Unauthorized, InternalServerError],
    Book,
    EndpointMiddleware.None
  ] =
    Endpoint(
      (RoutePattern.POST / path) ?? Doc.p("Endpoint for creating book")
    )
      .header(authHeader)
      .in[Book](Doc.p("Book"))
      .examplesIn(
        (
          "Example #1",
          (
            "",
            Book(
              None,
              "Java: эффективное программирование",
              None,
              Some("9785604139448"),
              Some("Третье издание"),
              Some(2019),
              Some(464),
              None,
              Some("Современные методики разработки на Java"),
              Some("RU"),
              Some("Programming"),
              Some(Publisher(None, "Диалектика", "Россия")),
              Some(Author(None, "Джошуа Блох", Some("USA")))
            )
          )
        )
      )
      .out[Book](Doc.p("Created book"))
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )

  /** Маршрут API добавления книги. */
  val route: Route[BookRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, book: Book) =>
        for {
          secret <- Security.secret
          result <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
            *> BookService.create(book).mapError(Right(_))
        } yield result
      )
    )

end PostBook
