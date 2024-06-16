package itcube.rest

import pdi.jwt.{Jwt, JwtAlgorithm, JwtClaim}
import zio.ZIO

import java.util.UUID
import scala.util.Try

/** Операции с JSON Web Token. */
object JsonWebToken:

  given clock: java.time.Clock = java.time.Clock.systemUTC()

  private val secretKey = "LibrariumSecretKey"

  def secret: String = secretKey

  /** Закодировать JWT. */
  def jwtEncode(userId: UUID, secretKey: String = secretKey): String =
    Jwt.encode(
      JwtClaim(subject = Some(userId.toString)).issuedNow.expiresIn(3600),
      secretKey,
      JwtAlgorithm.HS256
    )

  /** Раскодировать JWT. */
  def jwtDecode(token: String, secretKey: String = secretKey): Try[JwtClaim] =
    Jwt.decode(token, secretKey, Seq(JwtAlgorithm.HS256))

  /** Произвести валидацию JWT. */
  def jwtValidate(
      token: String,
      secretKey: String = secretKey
  ): ZIO[Any, AuthenticationError, String] =
    ZIO
      .fromTry(jwtDecode(token, secretKey))
      .orElseFail(AuthenticationError("Invalid or expired token"))
      .flatMap(claim =>
        ZIO
          .fromOption(claim.subject)
          .orElseFail(AuthenticationError("Missing subject claim"))
      )
  end jwtValidate

end JsonWebToken
