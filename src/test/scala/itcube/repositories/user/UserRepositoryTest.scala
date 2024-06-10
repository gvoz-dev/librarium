package itcube.repositories.user

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.entities.User
import itcube.repositories.RepoLayers
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object UserRepositoryTest extends ZIOSpecDefault:

  private def userRepoSpec: Spec[UserRepository, Throwable] =
    suite("User repository CRUD functions")(
      test("#all should return 2 users") {
        for {
          users <- UserRepository.all
          _ <- Console.printLine(users)
        } yield assertTrue(
          users.nonEmpty,
          users.size == 2
        )
      },
      test("#findById should return the user if it exists") {
        for {
          user <- UserRepository.findById(
            "ea962bb3-8f66-4256-bea5-8851c8f37dfb"
          )
          _ <- Console.printLine(user)
        } yield assertTrue(
          user.isDefined,
          user.get.name == "admin"
        )
      },
      test("#findById should return none if the user does not exist") {
        for {
          user <- UserRepository.findById(
            "37d706ed-9591-4fd3-8811-9970194347da"
          )
          _ <- Console.printLine(user)
        } yield assertTrue(
          user.isEmpty
        )
      },
      test("#findByEmail should return the user if it exists") {
        for {
          user <- UserRepository.findByEmail("admin@example.com")
          _ <- Console.printLine(user)
        } yield assertTrue(
          user.isDefined,
          user.get.name == "admin"
        )
      },
      test("#findByEmail should return none if the user does not exist") {
        for {
          user <- UserRepository.findByEmail("adm1n@example.com")
          _ <- Console.printLine(user)
        } yield assertTrue(
          user.isEmpty
        )
      },
      test("#findByName should return the user if it exists") {
        for {
          user <- UserRepository.findByName("roman")
          _ <- Console.printLine(user)
        } yield assertTrue(
          user.isDefined,
          user.get.id.contains(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c")
          )
        )
      },
      test("#findByName should return none if the user does not exist") {
        for {
          user <- UserRepository.findByName("tester")
          _ <- Console.printLine(user)
        } yield assertTrue(
          user.isEmpty
        )
      },
      test("#create user") {
        val user = User(None, "tester", "tester@example.com", "test", "user")
        for {
          inserted <- UserRepository.create(user)
          _ <- Console.printLine(inserted)
          selected <- UserRepository.findByName("tester")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          inserted.isDefined,
          selected.isDefined,
          inserted.get.id == selected.get.id
        )
      },
      test("#update user") {
        val uuid = UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c")
        val user =
          User(Some(uuid), "gvoz-dev", "roman@example.com", "qwerty", "user")
        for {
          updated <- UserRepository.update(user)
          selected <- UserRepository.findByName("gvoz-dev")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          updated.isDefined,
          selected.isDefined,
          selected.get.password == "qwerty"
        )
      },
      test("#delete user") {
        for {
          user <- UserRepository.findByName("tester")
          _ <- Console.printLine(user)
          _ <- UserRepository.delete(user.get.id.map(_.toString).get)
          deleted <- UserRepository.findByName("tester")
        } yield assertTrue(
          user.isDefined,
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("User repository")(
      userRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.userRepoLayer
      )

end UserRepositoryTest
