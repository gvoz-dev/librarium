package itcube.utils

import itcube.utils.Errors.AuthenticationError
import pdi.jwt.*
import zio.ZIO

import java.util.UUID
import scala.util.Try

/** Операции с JSON Web Token. */
object JsonWebToken:

  given clock: java.time.Clock = java.time.Clock.systemUTC()

  /** Закодировать JWT. */
  def encodeJwt(userId: UUID, secretKey: String): String =
    Jwt.encode(
      JwtClaim(subject = Some(userId.toString)).issuedNow.expiresIn(3600),
      secretKey,
      JwtAlgorithm.HS256
    )

  /** Раскодировать JWT. */
  def decodeJwt(token: String, secretKey: String): Try[JwtClaim] =
    Jwt.decode(token, secretKey, Seq(JwtAlgorithm.HS256))

  /** Произвести валидацию JWT. */
  def validateJwt(
      token: String,
      secretKey: String
  ): ZIO[Any, AuthenticationError, String] =
    ZIO
      .fromTry(decodeJwt(token, secretKey))
      .orElseFail(AuthenticationError("Invalid or expired token"))
      .flatMap(claim =>
        ZIO
          .fromOption(claim.subject)
          .orElseFail(AuthenticationError("Missing subject claim"))
      )
  end validateJwt

end JsonWebToken
