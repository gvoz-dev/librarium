package itcube

import itcube.repositories.author.AuthorRepository
import itcube.rest.api.*
import zio.http.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.openapi.*

package object rest:

  /** Маршруты REST. */
  object RestApiRoutes:

    /** Компиляция конечных точек. */
    private val endpoints =
      AuthorApi.endpoints

    /** Компиляция маршрутов API. */
    private val routes =
      AuthorApi.routes

    /** Генерация OpenAPI из конечных точек. */
    private val openAPI = OpenAPIGen.fromEndpoints(
      title = "Librarium API",
      version = "1.0",
      endpoints
    )

    /** Маршрут SwaggerUI. */
    private val swaggerRoutes =
      SwaggerUI.routes("docs" / "openapi", openAPI)

    /** Маршруты API и сгенерированного SwaggerUI. */
    def apply(): Routes[AuthorRepository, Response] = routes ++ swaggerRoutes

  end RestApiRoutes

end rest
