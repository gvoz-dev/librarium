package itcube.rest

import itcube.rest.api.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.openapi.*

/** Маршруты REST. */
object RestApiRoutes:

  /** Компиляция конечных точек. */
  private val endpoints =
    LoginApi.endpoints ++ AuthorApi.endpoints

  /** Компиляция маршрутов API. */
  private val routes =
    LoginApi.routes ++ AuthorApi.routes

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
  def apply() = routes ++ swaggerRoutes

end RestApiRoutes
