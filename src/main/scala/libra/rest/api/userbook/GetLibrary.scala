package libra.rest.api.userbook

import libra.repositories.userbook.UserBookRepository
import libra.services.userbook.UserBookService
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API получения библиотеки пользователя.
  *
  *   - GET /api/v1/library/{userId}
  */
object GetLibrary:

  private val path = "api" / "v1" / "library" / PathCodec.uuid("userId")

  /** Конечная точка API получения библиотеки пользователя. */
  val endpoint: Endpoint[
    UUID,
    UUID,
    Either[InternalServerError, NotFound],
    List[UUID],
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying books from library"))
      .out[List[UUID]](Doc.p("Books from library"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения библиотеки пользователя. */
  val route: Route[UserBookRepository, Nothing] =
    endpoint.implement(
      handler((userId: UUID) =>
        UserBookService
          .libraryBooks(userId)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetLibrary
