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
  *   - GET /api/v1/comments
  *   - GET /api/v1/comments?user={userId}
  *   - GET /api/v1/comments?book={bookId}
  *   - GET /api/v1/comments?user={userId}&book={bookId}
  */
object GetComments:

  private val path = "api" / "v1" / "comments"

  /** Конечная точка API получения комментариев на книгу. */
  val endpoint: Endpoint[
    Unit,
    (Option[UUID], Option[UUID]),
    Either[InternalServerError, NotFound],
    List[Comment],
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying comments"))
      .query(QueryCodec.queryTo[UUID]("user").optional ?? Doc.p("Query parameter to search comments by user"))
      .query(QueryCodec.queryTo[UUID]("book").optional ?? Doc.p("Query parameter to search comments by book"))
      .out[List[Comment]](Doc.p("Comments"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения комментариев на книгу. */
  val route: Route[CommentRepository, Nothing] =
    endpoint.implement(
      handler((userId: Option[UUID], bookId: Option[UUID]) =>
        val result = (userId, bookId) match
          case (None, None)             => CommentService.all
          case (Some(user), None)       => CommentService.findByUser(user)
          case (None, Some(book))       => CommentService.findByBook(book)
          case (Some(user), Some(book)) => CommentService.findByUserAndBook(user, book)

        result.mapError {
          case err: InternalServerError => Left(err)
          case err: NotFound            => Right(err)
        }
      )
    )

end GetComments
