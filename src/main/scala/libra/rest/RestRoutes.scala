package libra.rest

import libra.config.SecurityConfig
import libra.repositories.author.AuthorRepository
import libra.repositories.book.BookRepository
import libra.repositories.comment.CommentRepository
import libra.repositories.publisher.PublisherRepository
import libra.repositories.user.UserRepository
import libra.repositories.userbook.UserBookRepository
import libra.rest
import libra.rest.api.author.*
import libra.rest.api.book.*
import libra.rest.api.comment.*
import libra.rest.api.login.*
import libra.rest.api.publisher.*
import libra.rest.api.user.*
import libra.rest.api.userbook.*
import zio.http.*
import zio.http.Middleware.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.openapi.*

/** Маршруты REST. */
object RestRoutes:

  /** Коллекция конечных точек. */
  private val endpoints =
    List(
      Login.endpoint,
      Registration.endpoint,
      // Books
      GetBooks.endpoint,
      GetBookById.endpoint,
      PostBook.endpoint,
      PutBook.endpoint,
      DeleteBook.endpoint,
      // Users
      GetUsers.endpoint,
      GetUserById.endpoint,
      PutUser.endpoint,
      DeleteUser.endpoint,
      // Library
      GetLibrary.endpoint,
      PostLibraryAdd.endpoint,
      PostLibraryDelete.endpoint,
      // Progress
      GetProgress.endpoint,
      PostProgress.endpoint,
      // Rating
      GetAvgRating.endpoint,
      GetRating.endpoint,
      PostRating.endpoint,
      // Comments
      GetCommentById.endpoint,
      GetUserComments.endpoint,
      GetCommentsOnBook.endpoint,
      GetUserCommentsOnBook.endpoint,
      GetCommentById.endpoint,
      PostComment.endpoint,
      PutComment.endpoint,
      DeleteComment.endpoint,
      // Authors
      GetAuthors.endpoint,
      GetAuthorById.endpoint,
      PostAuthor.endpoint,
      PutAuthor.endpoint,
      DeleteAuthor.endpoint,
      // Publishers
      GetPublishers.endpoint,
      GetPublisherById.endpoint,
      PostPublisher.endpoint,
      PutPublisher.endpoint,
      DeletePublisher.endpoint
    )

  /** Коллекция маршрутов API. */
  private val routes =
    Routes(
      Login.route,
      Registration.route,
      // Books
      GetBooks.route,
      GetBookById.route,
      PostBook.route,
      PutBook.route,
      DeleteBook.route,
      // Users
      GetUsers.route,
      GetUserById.route,
      PutUser.route,
      DeleteUser.route,
      // Library
      GetLibrary.route,
      PostLibraryAdd.route,
      PostLibraryDelete.route,
      // Progress
      GetProgress.route,
      PostProgress.route,
      // Rating
      GetAvgRating.route,
      GetRating.route,
      PostRating.route,
      // Comments
      GetCommentById.route,
      GetUserComments.route,
      GetCommentsOnBook.route,
      GetUserCommentsOnBook.route,
      GetCommentById.route,
      PostComment.route,
      PutComment.route,
      DeleteComment.route,
      // Authors
      GetAuthors.route,
      GetAuthorById.route,
      PostAuthor.route,
      PutAuthor.route,
      DeleteAuthor.route,
      // Publishers
      GetPublishers.route,
      GetPublisherById.route,
      PostPublisher.route,
      PutPublisher.route,
      DeletePublisher.route
    )

  /** Генерация OpenAPI из конечных точек. */
  private val openAPI = OpenAPIGen.fromEndpoints(
    title = "Librarium API",
    version = "1.0",
    endpoints
  )

  /** Маршрут SwaggerUI. */
  private val swaggerRoutes =
    SwaggerUI.routes("docs" / "openapi", openAPI)

  /** Конфигурация CORS. */
  private val corsConfig: CorsConfig = CorsConfig()

  /** Маршруты API и сгенерированного SwaggerUI. */
  def apply(): Routes[
    UserRepository & BookRepository & UserBookRepository & CommentRepository & AuthorRepository & PublisherRepository &
      SecurityConfig,
    Response
  ] = (routes ++ swaggerRoutes) @@ cors(corsConfig)

end RestRoutes
