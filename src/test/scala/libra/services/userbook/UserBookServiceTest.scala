package libra.services.userbook

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import zio.Scope
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object UserBookServiceTest extends ZIOSpecDefault:

  private def userBookServiceSpec =
    suite("UserBook service functions")(
      test("#findUserBook should return id") {
        for {
          id <- UserBookService.findUserBook(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"),
            UUID.fromString("b43e5b87-a042-461b-8728-653eddced002")
          )
        } yield assertTrue(
          id.toString == "af1add3d-fc76-4ca0-ae99-5194c65f9af5"
        )
      },
      test("#libraryBooks should return all user's books") {
        for {
          books <- UserBookService
            .libraryBooks(UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"))
        } yield assertTrue(
          books.nonEmpty,
          books.size == 1,
          books.head == UUID.fromString("b43e5b87-a042-461b-8728-653eddced002")
        )
      },
      test("#addToLibrary should add book to the user's library") {
        for {
          _     <- UserBookService.addToLibrary(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"),
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
          books <- UserBookService
            .libraryBooks(UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"))
        } yield assertTrue(
          books.size == 2,
          books.contains(
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
        )
      },
      test("#setProgress & #getProgress should match") {
        for {
          _        <- UserBookService.setProgress(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"),
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252"),
            95.0f
          )
          progress <- UserBookService.getProgress(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"),
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
        } yield assertTrue(
          progress == 95.0f
        )
      },
      test("#setRating & #getRating & avgRating should match") {
        for {
          _      <- UserBookService.setRating(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"),
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252"),
            5
          )
          rating <- UserBookService.getRating(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"),
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
          avg    <- UserBookService
            .avgRating(UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252"))
        } yield assertTrue(
          rating == 5,
          avg == 5.0f
        )
      },
      test("#deleteFromLibrary should delete book from the user's library") {
        for {
          _     <- UserBookService.deleteFromLibrary(
            UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"),
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
          books <- UserBookService
            .libraryBooks(UUID.fromString("ca3e509d-06cf-4655-802a-7f8355339e2c"))
        } yield assertTrue(
          books.size == 1,
          !books.contains(
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("UserBook service tests")(
      userBookServiceSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        TestRepoLayers.userBookRepoLayer
      )

end UserBookServiceTest
