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

/** API получения комментария по идентификатору.
  *
  *   - GET /api/v1/comments/{id}
  */
object GetCommentById:

  private val path = "api" / "v1" / "comments" / PathCodec.uuid("id")

  /** Конечная точка API получения комментария по идентификатору. */
  val endpoint: Endpoint[
    UUID,
    UUID,
    Either[InternalServerError, NotFound],
    Comment,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.GET / path) ?? Doc.p("Querying comment by ID"))
      .out[Comment](Doc.p("Comment"))
      .outError[NotFound](Status.NotFound)
      .outError[InternalServerError](Status.InternalServerError)

  /** Маршрут API получения комментария по идентификатору. */
  val route: Route[CommentRepository, Nothing] =
    endpoint.implement(
      handler((id: UUID) =>
        CommentService
          .findById(id)
          .mapError {
            case err: InternalServerError => Left(err)
            case err: NotFound            => Right(err)
          }
      )
    )

end GetCommentById
