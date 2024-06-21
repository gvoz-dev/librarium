package libra.utils

import libra.utils.JsonWebToken.*
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
          jwt <- ZIO.succeed(encodeJwt(uuid, "user", secret))
          jwtClaim <- ZIO.fromTry(decodeJwt(jwt, secret))
          claim <- validateJwt(jwt, secret)
        } yield assertTrue(
          jwtClaim.isValid,
          jwtClaim.subject.isDefined,
          claim.subject == userId,
          claim.content.role == "user"
        )
      },
      test("#checkTokenPermissions is correct") {
        for {
          userClaim <- ZIO.succeed(Payload("123", PayloadContent("user")))
          permitted <- ZIO.succeed(checkTokenPermissions(userClaim, "123"))
          notPermitted <- ZIO.succeed(checkTokenPermissions(userClaim, "789"))
          adminClaim <- ZIO.succeed(Payload("456", PayloadContent("admin")))
          adminIsPermitted <- ZIO.succeed(
            checkTokenPermissions(adminClaim, "789")
          )
        } yield assertTrue(
          permitted,
          !notPermitted,
          adminIsPermitted
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("JWT tests")(jwtSpec)

end JsonWebTokenTest
