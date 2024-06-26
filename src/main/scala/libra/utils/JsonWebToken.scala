package libra.utils

import libra.utils.ServiceError.*
import pdi.jwt.*
import zio.ZIO
import zio.json.*

import java.util.UUID
import scala.util.Try

/** Операции с JSON Web Token. */
object JsonWebToken:

  given java.time.Clock = java.time.Clock.systemUTC()

  /** Закодировать JWT.
    *
    * @param userId
    *   уникальный идентификатор пользователя
    * @param userRole
    *   роль пользователя
    * @param secretKey
    *   секретный ключ
    */
  def encodeJwt(userId: UUID, userRole: String, secretKey: String): String =
    Jwt.encode(
      JwtClaim()
        .about(userId.toString)
        .+("role", userRole)
        .issuedNow
        .expiresIn(36000), // TODO: В целях тестирования токен действует 10 часов
      secretKey,
      JwtAlgorithm.HS256
    )

  /** Раскодировать JWT.
    *
    * @param token
    *   JSON Web Token
    * @param secretKey
    *   секретный ключ
    */
  def decodeJwt(token: String, secretKey: String): Try[JwtClaim] =
    Jwt.decode(token, secretKey, Seq(JwtAlgorithm.HS256))

  /** Произвести валидацию JWT.
    *
    * Функция возвращает полезную нагрузку токена при успешном выполнении.
    *
    * @param token
    *   JSON Web Token
    * @param secretKey
    *   секретный ключ
    */
  def validateJwt(
      token: String,
      secretKey: String
  ): ZIO[Any, Unauthorized, Payload] =
    ZIO
      .fromTry(decodeJwt(token, secretKey))
      .orElseFail(Unauthorized("Invalid or expired token!"))
      .flatMap(claim =>
        for {
          subject <- ZIO
            .fromOption(claim.subject)
            .orElseFail(Unauthorized("Missing subject claim!"))
          content <- ZIO
            .fromEither(claim.content.fromJson[PayloadContent])
            .onError(e => ZIO.logError(e.prettyPrint))
            .orElseFail(Unauthorized("Missing content!"))
        } yield Payload(subject, content)
      )
  end validateJwt

  /** Полезная нагрузка токена.
    *
    * @param subject
    *   идентификатор пользователя
    * @param content
    *   дополнительный контент (роль пользователя, ?)
    */
  final case class Payload(subject: String, content: PayloadContent)

  /** Дополнительный контент в полезной нагрузке токена.
    *
    * @param role
    *   роль пользователя
    */
  final case class PayloadContent(role: String)

  /** Автоматический вывод JSON-декодера для дополнительного контента токена. */
  given JsonDecoder[PayloadContent] = DeriveJsonDecoder.gen

  /** Проверить права токена на доступ к ресурсу.
    *
    * Функция возвращает true, если права есть.
    *
    * @param payload
    *   полезная нагрузка токена
    * @param ownerId
    *   идентификатор владельца ресурса
    */
  def checkTokenPermissions(payload: Payload, ownerId: String): Boolean =
    payload.subject == ownerId || payload.content.role == "admin" // TODO: Временная реализация

end JsonWebToken
