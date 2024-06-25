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

/** API получения комментариев на книгу.
  *
  *   - GET /api/v1/comments/book/{bookId}
  */
object GetCommentsOnBook:

  private val path = "api" / "v1" / "comments" / "book" / PathCodec.uuid("id")

  /** Конечная точка API получения комментариев на книгу. */
  val endpoint: Endpoint[
    UUID,
    UUID,
    Either[InternalServerError, NotFound],
    List[Comment],
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Endpoint for querying comments on book"))
      .out[List[Comment]](Doc.p("Comments"))
      .outError[NotFound](
        Status.NotFound,
        Doc.p("Not found error")
      )
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )

  /** Маршрут API получения комментариев на книгу. */
  val route: Route[CommentRepository, Nothing] =
    endpoint.implement(
      handler((id: UUID) =>
        CommentService
          .findByBook(id)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetCommentsOnBook
