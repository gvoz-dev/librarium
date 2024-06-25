package libra.rest.api.comment

import libra.entities.Comment
import libra.repositories.comment.CommentRepository
import libra.services.comment.CommentService
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API получения комментариев пользователя на книгу.
  *
  *   - GET /api/v1/comments/{userId}/{bookId}
  */
object GetUserCommentsOnBook:

  private val path = "api" / "v1" / "comments" / "ub" / PathCodec.uuid("userId") / PathCodec.uuid("bookId")

  /** Конечная точка API получения комментариев пользователя на книгу. */
  val endpoint: Endpoint[
    (UUID, UUID),
    (UUID, UUID),
    Either[InternalServerError, NotFound],
    List[Comment],
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Endpoint for querying comments by user on book"))
      .out[List[Comment]](Doc.p("Comments"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения комментариев пользователя на книгу. */
  val route: Route[CommentRepository, Nothing] =
    endpoint.implement(
      handler((userId: UUID, bookId: UUID) =>
        CommentService
          .findByUserAndBook(userId, bookId)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetUserCommentsOnBook
