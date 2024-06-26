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

/** API получения прогресса прочитанного.
  *
  *   - GET /api/v1/progress/{bookId}/{userId}
  */
object GetProgress:

  private val path = "api" / "v1" / "progress" / PathCodec.uuid("bookId") / PathCodec.uuid("userId")

  /** Конечная точка API получения прогресса прочитанного. */
  val endpoint: Endpoint[
    (UUID, UUID),
    (UUID, UUID),
    Either[InternalServerError, NotFound],
    Progress,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying reading progress"))
      .out[Progress](Doc.p("Progress"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения прогресса прочитанного. */
  val route: Route[UserBookRepository, Nothing] =
    endpoint.implement(
      handler((bookId: UUID, userId: UUID) =>
        UserBookService
          .getProgress(userId, bookId)
          .mapBoth(
            {
              case err: InternalServerError => Left(err)
              case err: NotFound            => Right(err)
            },
            value => Progress(value)
          )
      )
    )

end GetProgress
