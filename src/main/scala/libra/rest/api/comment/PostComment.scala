package libra.rest.api.comment

import libra.config.SecurityConfig
import libra.entities.Comment
import libra.repositories.comment.CommentRepository
import libra.repositories.userbook.UserBookRepository
import libra.rest.api.authHeader
import libra.services.comment.CommentService
import libra.utils.*
import libra.utils.ServiceError.*
import zio.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.*

import java.util.UUID

/** API добавления комментария.
  *
  *   - POST /api/v1/comments/{bookId}/{userId}
  */
object PostComment:

  private val path = "api" / "v1" / "comments"

  /** Конечная точка API добавления комментария. */
  val endpoint: Endpoint[
    Unit,
    (String, Comment),
    Either[Unauthorized, InternalServerError],
    Comment,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.POST / path) ?? Doc.p("Creating comment"))
      .header(authHeader)
      .in[Comment](Doc.p("Comment"))
      .examplesIn(
        (
          "Example #1",
          (
            "",
            Comment(
              None,
              UUID.fromString("ea962bb3-8f66-4256-bea5-8851c8f37dfb"),
              UUID.fromString("b43e5b87-a042-461b-8728-653eddced002"),
              "Cool book!",
              false,
              None,
              None
            )
          )
        )
      )
      .out[Comment](Status.Created)
      .outError[InternalServerError](Status.InternalServerError)
      .outError[Unauthorized](Status.Unauthorized)

  /** Маршрут API добавления комментария. */
  val route: Route[UserBookRepository & CommentRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((token: String, comment: Comment) =>
        for {
          secret <- Security.secret
          claim  <- JsonWebToken.validateJwt(token, secret).mapError(Left(_))
          result <-
            if JsonWebToken.checkTokenPermissions(claim, comment.userId.toString) then
              CommentService.create(comment).mapError(Right(_))
            else ZIO.fail(Left(Unauthorized("No permission")))
        } yield result
      )
    )

end PostComment
