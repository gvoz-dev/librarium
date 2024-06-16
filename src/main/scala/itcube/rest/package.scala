package itcube

import zio.http.*
import zio.http.codec.*
import zio.schema.*

package object rest:

  /** Кодек для HTTP-заголовка авторизации.
    *
    * Примечание: стандартный заголовок "Authorization" работает отлично при
    * тестировании в CURL, но не в сгенерированном SwaggerUI, поэтому (как
    * временное решение) используется кастомный заголовок для передачи токена.
    */
  val authHeader: HeaderCodec[String] = HttpCodec.name[String]("X-JWT-Auth")

  /** Ошибка HTTP. */
  sealed trait HttpError

  /** Ошибка аутентификации пользователя. */
  case class AuthenticationError(message: String) extends HttpError

  object AuthenticationError:
    given schema: Schema[AuthenticationError] = DeriveSchema.gen

  /** Некорректный запрос. */
  case class BadRequestError(message: String) extends HttpError

  object BadRequestError:
    given schema: Schema[BadRequestError] = DeriveSchema.gen

end rest
