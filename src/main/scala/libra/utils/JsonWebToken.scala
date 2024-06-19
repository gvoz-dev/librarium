package libra.utils

import libra.utils.ServiceError.*
import pdi.jwt.*
import zio.ZIO
import zio.json.*

import java.util.UUID
import scala.util.Try

/** Операции с JSON Web Token. */
object JsonWebToken:

  given clock: java.time.Clock = java.time.Clock.systemUTC()

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
        .expiresIn(3600),
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
  ): ZIO[Any, AuthenticationError, Claim] =
    ZIO
      .fromTry(decodeJwt(token, secretKey))
      .orElseFail(AuthenticationError("Invalid or expired token!"))
      .flatMap(claim =>
        for {
          subject <- ZIO
            .fromOption(claim.subject)
            .orElseFail(AuthenticationError("Missing subject claim!"))
          content <- ZIO
            .fromEither(claim.content.fromJson[ClaimContent])
            .onError(e => ZIO.logError(e.prettyPrint))
            .orElseFail(AuthenticationError("Missing content!"))
        } yield Claim(subject, content)
      )
  end validateJwt

  /** Полезная нагрузка токена.
    *
    * @param subject
    *   идентификатор пользователя
    * @param content
    *   дополнительный контент (роль пользователя, ?)
    */
  final case class Claim(subject: String, content: ClaimContent)

  /** Дополнительный контент в полезной нагрузке токена.
    *
    * @param role
    *   роль пользователя
    */
  final case class ClaimContent(role: String)

  /** Автоматический вывод JSON-декодера для дополнительного контента токена. */
  given JsonDecoder[ClaimContent] = DeriveJsonDecoder.gen

  /** Проверить права токена на доступ к ресурсу.
    *
    * Функция возвращает true, если права есть.
    *
    * @param claim
    *   полезная нагрузка токена
    * @param ownerId
    *   идентификатор владельца ресурса
    */
  def checkTokenPermissions(claim: Claim, ownerId: String): Boolean =
    claim.subject == ownerId || claim.content.role == "admin"

end JsonWebToken
