package itcube.repositories.userbook

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.repositories.RepoLayers
import itcube.services.userbook.UserBookService
import zio.Scope
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object UserBookRepositoryTest extends ZIOSpecDefault:

  private def userBookRepoSpec =
    suite("UserBook repository/service functions")(
      test("#findUserBook should return id") {
        for {
          id <- UserBookService.findUserBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          id.map(_.toString).contains("af1add3d-fc76-4ca0-ae99-5194c65f9af5")
        )
      },
      test("#libraryBooks should return all user's books") {
        for {
          books <- UserBookService.libraryBooks(
            "ca3e509d-06cf-4655-802a-7f8355339e2c"
          )
        } yield assertTrue(
          books.nonEmpty,
          books.size == 1,
          books.head == UUID.fromString("b43e5b87-a042-461b-8728-653eddced002")
        )
      },
      test("#addToLibrary should add book to the user's library") {
        for {
          _ <- UserBookService.addToLibrary(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          books <- UserBookService.libraryBooks(
            "ca3e509d-06cf-4655-802a-7f8355339e2c"
          )
        } yield assertTrue(
          books.size == 2,
          books.contains(
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
        )
      },
      test("#setProgress & #getProgress should match") {
        for {
          _ <- UserBookService.setProgress(
            95.0f,
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          progress <- UserBookService.getProgress(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
        } yield assertTrue(
          progress.contains(95.0f)
        )
      },
      test("#setRating & #getRating & avgRating should match") {
        for {
          _ <- UserBookService.setRating(
            5,
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          rating <- UserBookService.getRating(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          avg <- UserBookService.avgRating(
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
        } yield assertTrue(
          rating.contains(5),
          avg.contains(5.0f)
        )
      },
      test("#deleteFromLibrary should delete book from the user's library") {
        for {
          _ <- UserBookService.deleteFromLibrary(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          books <- UserBookService.libraryBooks(
            "ca3e509d-06cf-4655-802a-7f8355339e2c"
          )
        } yield assertTrue(
          books.size == 1,
          !books.contains(
            UUID.fromString("eb98fd47-793e-448c-ad50-0a68d1f76252")
          )
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("UserBook repository/service")(
      userBookRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.userBookRepoLayer
      )

end UserBookRepositoryTest
