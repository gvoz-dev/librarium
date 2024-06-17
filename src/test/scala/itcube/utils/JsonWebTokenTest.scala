package itcube.utils

import itcube.utils.JsonWebToken.{decodeJwt, encodeJwt, validateJwt}
import zio.*
import zio.test.*

import java.util.UUID

object JsonWebTokenTest extends ZIOSpecDefault:

  given clock: java.time.Clock = java.time.Clock.systemUTC()

  private val secret = "test_secret"
  private val userId = "ea962bb3-8f66-4256-bea5-8851c8f37dfb"
  private val uuid = UUID.fromString(userId)

  private def jwtSpec =
    suite("JWT functions")(
      test("#encodeJwt & #decodeJwt & #validateJwt are correct") {
        for {
          jwt <- ZIO.succeed(encodeJwt(uuid, secret))
          claim <- ZIO.fromTry(decodeJwt(jwt, secret))
          subject <- validateJwt(jwt, secret)
        } yield assertTrue(
          claim.subject.isDefined,
          claim.isValid,
          subject == userId
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("JWT tests")(
      jwtSpec
    )

end JsonWebTokenTest
