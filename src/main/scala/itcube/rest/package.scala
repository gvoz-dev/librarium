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

  /** Учётные данные пользователя.
    *
    * @param email
    *   адрес электронной почты
    * @param password
    *   пароль
    */
  case class Credentials(email: String, password: String)

  object Credentials:
    given schema: Schema[Credentials] = DeriveSchema.gen

  /** Токен аутентификации.
    *
    * @param jwt
    *   JSON Web Token
    */
  case class Token(jwt: String)

  object Token:
    given schema: Schema[Token] = DeriveSchema.gen

end rest
