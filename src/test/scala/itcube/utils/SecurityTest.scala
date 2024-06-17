package itcube.utils

import itcube.utils.Security.*
import zio.*
import zio.test.*

object SecurityTest extends ZIOSpecDefault:

  private def hashingPasswordsSpec =
    suite("Password hashing functions")(
      test("#hashing same passwords should return same results") {
        for {
          hash1 <- hashPassword("12345")
          hash2 <- hashPassword("12345")
          _ <- Console.printLine(hash1)
          isValid <- validatePassword("12345", hash1)
        } yield assertTrue(
          hash1 == hash2,
          isValid
        )
      },
      test("#hashing different passwords should return different results") {
        for {
          hash1 <- hashPassword("12345")
          hash2 <- hashPassword("qwe")
          _ <- Console.printLine(hash1)
          _ <- Console.printLine(hash2)
          isValid <- validatePassword("12345", hash1)
          nonValid <- validatePassword("12345", hash2)
        } yield assertTrue(
          hash1 != hash2,
          isValid,
          !nonValid
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Security tests")(
      hashingPasswordsSpec
    )

end SecurityTest