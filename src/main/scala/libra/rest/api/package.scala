package libra.rest

import zio.http.*
import zio.http.codec.*
import zio.schema.*
import zio.schema.annotation.*
import zio.schema.validation.*

package object api:

  /** Кодек для HTTP-заголовка авторизации.
    *
    * Примечание: стандартный заголовок "Authorization" работает отлично при тестировании в CURL, но не в
    * сгенерированном SwaggerUI, поэтому (как временное решение) используется кастомный заголовок для передачи токена.
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
      @validate(Validation.email)
      email: String,
      @description("User password")
      @validate(Validation.minLength(1))
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

  /** Прогресс прочитанного.
    *
    * @param value
    *   значение прогресса
    */
  final case class Progress(
      @description("Progress value")
      @validate(Validation.between[Float](0.0f, 100.0f)(NumType.FloatType))
      value: Float
  )

  /** Гивен ZIO-схемы прогресса прочитанного. */
  given Schema[Progress] = DeriveSchema.gen

  /** Пользовательский рейтинг книги.
    *
    * @param value
    *   значение рейтинга
    */
  final case class Rating(
      @description("Rating value")
      @validate(Validation.between[Int](0, 5)(NumType.IntType))
      value: Int
  )

  /** Гивен ZIO-схемы пользовательского рейтинга книги. */
  given Schema[Rating] = DeriveSchema.gen

  /** Средний рейтинг книги.
    *
    * @param value
    *   значение среднего рейтинга
    */
  final case class AvgRating(
      @description("Avg rating value")
      value: Float
  )

  /** Гивен ZIO-схемы среднего рейтинга книги. */
  given Schema[AvgRating] = DeriveSchema.gen

end api
