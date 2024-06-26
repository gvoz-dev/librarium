package libra.utils

import libra.TestSecurityConfig
import libra.utils.JsonWebToken.*
import zio.*
import zio.test.*

import java.util.UUID

object JsonWebTokenTest extends ZIOSpecDefault:

  given clock: java.time.Clock = java.time.Clock.systemUTC()

  private def jwtSpec =
    suite("JWT functions")(
      test("#encodeJwt/decodeJwt/validateJwt are correct") {
        val userId = "ea962bb3-8f66-4256-bea5-8851c8f37dfb"
        val uuid   = UUID.fromString(userId)
        for {
          secret   <- Security.secret
          jwt      <- ZIO.succeed(encodeJwt(uuid, "user", secret))
          jwtClaim <- ZIO.fromTry(decodeJwt(jwt, secret))
          claim    <- validateJwt(jwt, secret)
        } yield assertTrue(
          jwtClaim.isValid,
          jwtClaim.subject.isDefined,
          claim.subject == userId,
          claim.content.role == "user"
        )
      },
      test("#checkTokenPermissions is correct") {
        for {
          // Для пользователя должен совпадать ID
          userClaim  <- ZIO.succeed(Payload("123", PayloadContent("user")))
          ok         <- ZIO.succeed(checkTokenPermissions(userClaim, "123"))
          notOk      <- ZIO.succeed(checkTokenPermissions(userClaim, "789"))
          // Для администратора это не обязательно
          adminClaim <- ZIO.succeed(Payload("456", PayloadContent("admin")))
          alwaysOk   <- ZIO.succeed(checkTokenPermissions(adminClaim, "789"))
        } yield assertTrue(ok, !notOk, alwaysOk)
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("JWT tests")(jwtSpec).provideShared(TestSecurityConfig.live)

end JsonWebTokenTest
