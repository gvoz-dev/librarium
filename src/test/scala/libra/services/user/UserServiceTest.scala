package libra.services.user

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.entities.User
import libra.repositories.user.UserRepository
import libra.utils.ServiceError
import libra.utils.ServiceError.*
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*

import java.util.UUID

object UserServiceTest extends ZIOSpecDefault:

  private def userServiceSpec =
    suite("User service functions")(
      test("#all should return 2 users") {
        for {
          users <- UserService.all
        } yield assertTrue(
          users.nonEmpty,
          users.size == 2
        )
      },
      test("#findById should return the user if it exists") {
        for {
          user <- UserService
            .findById(UUID.fromString("ea962bb3-8f66-4256-bea5-8851c8f37dfb"))
        } yield assertTrue(
          user.name == "admin"
        )
      },
      test("#findById should fail if the user does not exist") {
        for {
          result <- UserService
            .findById(UUID.fromString("37d706ed-9591-4fd3-8811-9970194347da"))
            .exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#findByEmail should return the user if it exists") {
        for {
          user <- UserService.findByEmail("admin@example.com")
        } yield assertTrue(
          user.name == "admin"
        )
      },
      test("#findByEmail should fail if the user does not exist") {
        for {
          result <- UserService.findByEmail("adm1n@example.com").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#findByName should return the user if it exists") {
        for {
          users <- UserService.findByName("roman")
        } yield assertTrue(
          users.head.id.contains(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c")
          )
        )
      },
      test("#findByName should fail if the user does not exist") {
        for {
          result <- UserService.findByName("tester").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#create user is correct") {
        val user = User(None, "tester", "tester@example.com", "test")
        for {
          inserted <- UserService.create(user)
          selected <- UserService.findByName("tester")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.id == inserted.id
        )
      },
      test("#update user is correct") {
        val uuid = UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c")
        val user =
          User(Some(uuid), "gvoz-dev", "roman@example.com", "qwerty")
        for {
          updated  <- UserService.update(user)
          selected <- UserService.findByName("gvoz-dev")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.password == "qwerty"
        )
      },
      test("#delete user is correct") {
        for {
          users  <- UserService.findByName("tester")
          _      <- UserService.delete(users.head.id.get)
          result <- UserService.findByName("tester").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("User service tests")(
      userServiceSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        TestRepoLayers.userRepoLayer
      )

end UserServiceTest
