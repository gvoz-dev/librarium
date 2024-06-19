package libra

import zio.http.*
import zio.http.codec.*
import zio.schema.*
import zio.schema.annotation.description

package object rest:

  /** Кодек для HTTP-заголовка авторизации.
    *
    * Примечание: стандартный заголовок "Authorization" работает отлично при
    * тестировании в CURL, но не в сгенерированном SwaggerUI, поэтому (как
    * временное решение) используется кастомный заголовок для передачи токена.
    */
  val authHeader: HeaderCodec[String] =
    HttpCodec.name[String]("X-JWT-Auth") ?? Doc.p("JSON Web Token")

  /** Учётные данные пользователя.
    *
    * @param email
    *   адрес электронной почты
    * @param password
    *   пароль
    */
  final case class Credentials(
      @description("User email")
      email: String,
      @description("User password")
      password: String
  )

  /** Гивен ZIO-схемы учётных данных пользователя. */
  given Schema[Credentials] = DeriveSchema.gen

  /** Токен аутентификации.
    *
    * @param jwt
    *   JSON Web Token
    */
  final case class Token(
      @description("JSON Web Token")
      jwt: String
  )

  /** Гивен ZIO-схемы токена аутентификации. */
  given Schema[Token] = DeriveSchema.gen

end rest
