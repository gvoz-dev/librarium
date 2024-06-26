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

/** API получения среднего рейтинга книги.
  *
  *   - GET /api/v1/rating/{bookId}/avg
  */
object GetAvgRating:

  private val path = "api" / "v1" / "rating" / PathCodec.uuid("bookId") / "avg"

  /** Конечная точка API получения среднего рейтинга книги. */
  val endpoint: Endpoint[
    UUID,
    UUID,
    Either[InternalServerError, NotFound],
    AvgRating,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying average book rating"))
      .out[AvgRating](Doc.p("Average rating"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения среднего рейтинга книги. */
  val route: Route[UserBookRepository, Nothing] =
    endpoint.implement(
      handler((bookId: UUID) =>
        UserBookService
          .avgRating(bookId)
          .mapBoth(
            {
              case err: InternalServerError => Left(err)
              case err: NotFound            => Right(err)
            },
            value => AvgRating(value)
          )
      )
    )

end GetAvgRating
