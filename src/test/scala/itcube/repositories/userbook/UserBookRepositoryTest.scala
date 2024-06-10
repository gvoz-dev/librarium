package itcube.repositories.userbook

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.repositories.RepoLayers
import zio.Scope
import zio.test.*
import zio.test.TestAspect.*

object UserBookRepositoryTest extends ZIOSpecDefault:

  private def userBookRepoSpec =
    suite("UserBook repository functions")(
      test("#findUserBook should return id") {
        for {
          id <- UserBookRepository.findUserBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          id.map(_.toString).contains("af1add3d-fc76-4ca0-ae99-5194c65f9af5")
        )
      }
      // test("#deleteFromLibrary should delete book from the user's library")
      // test("#addToLibrary should add book to the user's library")
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("UserBook repository")(
      userBookRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.userBookRepoLayer
      )

end UserBookRepositoryTest
