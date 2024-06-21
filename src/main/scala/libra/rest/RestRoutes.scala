package libra.rest

import libra.config.SecurityConfig
import libra.repositories.author.AuthorRepository
import libra.repositories.user.UserRepository
import libra.rest
import libra.rest.api.author.*
import libra.rest.api.book.*
import libra.rest.api.login.*
import libra.rest.api.publisher.*
import libra.rest.api.user.*
import zio.http.Middleware.*
import zio.http.Routes
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
  def apply() = (routes ++ swaggerRoutes) @@ cors(corsConfig)

end RestRoutes
