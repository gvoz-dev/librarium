package libra.utils

import libra.utils.Security.*
import zio.*
import zio.test.*

object SecurityTest extends ZIOSpecDefault:

  private def hashingPasswordsSpec =
    suite("Password hashing functions")(
      test("#hashPassword same passwords should return same results") {
        for {
          hash1 <- hashPassword("12345")
          hash2 <- hashPassword("12345")
          isValid <- validatePassword("12345", hash1)
        } yield assertTrue(
          hash1 == hash2,
          isValid
        )
      },
      test(
        "#hashPassword different passwords should return different results"
      ) {
        for {
          hash1 <- hashPassword("12345")
          hash2 <- hashPassword("qwe")
          isValid <- validatePassword("12345", hash1)
          notValid <- validatePassword("12345", hash2)
        } yield assertTrue(
          hash1 != hash2,
          isValid,
          !notValid
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    suite("Security tests")(hashingPasswordsSpec)

end SecurityTest
