package libra.rest.api.comment

import libra.config.SecurityConfig
import libra.entities.Comment
import libra.repositories.comment.CommentRepository
import libra.rest.api.authHeader
import libra.services.comment.CommentService
import libra.utils.*
import libra.utils.JsonWebToken.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

/** API изменения комментария.
  *
  *   - PUT /api/v1/comments
  */
object PutComment:

  private val path = "api" / "v1" / "comments"

  /** Конечная точка API изменения комментария. */
  val endpoint: Endpoint[
    Unit,
    (String, Comment),
    Either[BadRequest, Either[Unauthorized, InternalServerError]],
    Comment,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.PUT / path) ?? Doc.p("Updating comment"))
      .header(authHeader)
      .in[Comment](Doc.p("Comment"))
      .out[Comment](Doc.p("Updated comment"))
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)
      .outError[BadRequest](Status.BadRequest)

  /** Маршрут API изменения комментария. */
  val route: Route[CommentRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, comment: Comment) =>
        for {
          secret     <- Security.secret
          claim      <- validateJwt(token, secret).mapError(err => Right(Left(err)))
          id         <-
            ZIO
              .getOrFail(comment.id)
              .orElseFail(Left(BadRequest("No comment ID")))
          oldComment <-
            CommentService
              .findById(id)
              .orElseFail(Left(BadRequest("Comment does not exist")))
          result     <-
            if comment.userId == oldComment.userId && comment.bookId == oldComment.bookId
              && checkTokenPermissions(claim, comment.userId.toString)
            then CommentService.update(comment).mapError(err => Right(Right(err)))
            else ZIO.fail(Right(Left(Unauthorized("No permission"))))
        } yield result
      )
    )

end PutComment
