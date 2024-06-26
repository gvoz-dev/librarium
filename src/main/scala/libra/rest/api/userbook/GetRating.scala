package libra.rest.api.userbook

import libra.repositories.userbook.UserBookRepository
import libra.rest.api.{*, given}
import libra.services.userbook.UserBookService
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API получения пользовательского рейтинга книги.
  *
  *   - GET /api/v1/rating/{bookId}/{userId}
  */
object GetRating:

  private val path = "api" / "v1" / "rating" / PathCodec.uuid("bookId") / PathCodec.uuid("userId")

  /** Конечная точка API получения пользовательского рейтинга книги. */
  val endpoint: Endpoint[
    (UUID, UUID),
    (UUID, UUID),
    Either[InternalServerError, NotFound],
    Rating,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying book rating"))
      .out[Rating](Doc.p("Rating"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения пользовательского рейтинга книги. */
  val route: Route[UserBookRepository, Nothing] =
    endpoint.implement(
      handler((bookId: UUID, userId: UUID) =>
        UserBookService
          .getRating(userId, bookId)
          .mapBoth(
            {
              case err: InternalServerError => Left(err)
              case err: NotFound            => Right(err)
            },
            value => Rating(value)
          )
      )
    )

end GetRating
