package itcube.rest

import itcube.rest.api.*
import zio.http.Middleware.*
import zio.http.codec.*
import zio.http.codec.PathCodec.*
import zio.http.endpoint.openapi.*

/** Маршруты REST. */
object RestApiRoutes:

  /** Коллекция конечных точек. */
  private val endpoints =
    LoginApi.endpoints ++ AuthorApi.endpoints

  /** Коллекция маршрутов API. */
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

  /** Конфигурация CORS. */
  private val corsConfig: CorsConfig = CorsConfig()

  /** Маршруты API и сгенерированного SwaggerUI. */
  def apply() = (routes ++ swaggerRoutes) @@ cors(corsConfig)

end RestApiRoutes
