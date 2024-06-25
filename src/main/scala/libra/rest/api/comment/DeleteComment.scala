package libra.rest.api.comment

import libra.config.SecurityConfig
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

import java.util.UUID

/** API удаления комментария.
  *
  *   - DELETE /api/v1/comments/{id}
  */
object DeleteComment:

  private val path = "api" / "v1" / "comments" / PathCodec.uuid("id")

  /** Конечная точка API удаления комментария. */
  val endpoint: Endpoint[
    UUID,
    (UUID, String),
    Either[BadRequest, Either[Unauthorized, InternalServerError]],
    Unit,
    EndpointMiddleware.None
  ] =
    Endpoint((RoutePattern.DELETE / path) ?? Doc.p("Endpoint for deleting comment"))
      .header(authHeader)
      .out[Unit](Status.NoContent)
      .outError[InternalServerError](
        Status.InternalServerError,
        Doc.p("Service error")
      )
      .outError[Unauthorized](
        Status.Unauthorized,
        Doc.p("Authorization error")
      )
      .outError[BadRequest](
        Status.BadRequest,
        Doc.p("Invalid comment")
      )

  /** Маршрут API удаления комментария. */
  val route: Route[CommentRepository & SecurityConfig, Nothing] =
    endpoint.implement(
      handler((id: UUID, token: String) =>
        for {
          secret  <- Security.secret
          claim   <- validateJwt(token, secret).mapError(err => Right(Left(err)))
          comment <- CommentService
            .findById(id)
            .orElseFail(Left(BadRequest("Comment does not exist")))
          _       <-
            if checkTokenPermissions(claim, comment.userId.toString) then
              CommentService
                .delete(id)
                .mapError(err => Right(Right(err)))
            else ZIO.fail(Right(Left(Unauthorized("No permission"))))
        } yield ()
      )
    )

end DeleteComment
