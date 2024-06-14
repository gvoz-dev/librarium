package itcube.repositories.author

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.entities.Author
import itcube.repositories.RepoLayers
import itcube.services.author.AuthorService
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object AuthorRepositoryTest extends ZIOSpecDefault:

  private def authorRepoSpec: Spec[AuthorRepository, Throwable] =
    suite("Author repository/service functions")(
      test("#all should return 3 authors") {
        for {
          authors <- AuthorService.all
          _ <- Console.printLine(authors)
        } yield assertTrue(
          authors.nonEmpty,
          authors.size == 3
        )
      },
      test("#findById should return the author if it exists") {
        for {
          author <- AuthorService.findById(
            "0584125f-74e9-4b2b-92e2-e7396803aaba"
          )
          _ <- Console.printLine(author)
        } yield assertTrue(
          author.isDefined,
          author.get.name == "Donald Knuth"
        )
      },
      test("#findById should return none if the author does not exist") {
        for {
          author <- AuthorService.findById(
            "37d706ed-9591-4fd3-8811-9970194347da"
          )
          _ <- Console.printLine(author)
        } yield assertTrue(
          author.isEmpty
        )
      },
      test("#findByName should return the author if it exists") {
        for {
          author <- AuthorService.findByName("Martin Odersky")
          _ <- Console.printLine(author)
        } yield assertTrue(
          author.isDefined,
          author.get.id.contains(
            UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")
          )
        )
      },
      test("#findByName should return none if the author does not exist") {
        for {
          author <- AuthorService.findByName("Frank Herbert")
          _ <- Console.printLine(author)
        } yield assertTrue(
          author.isEmpty
        )
      },
      test("#create author") {
        val author = Author(None, "Gvozdev Roman", Some("Russia"))
        for {
          inserted <- AuthorService.create(author)
          _ <- Console.printLine(inserted)
          selected <- AuthorService.findByName("Gvozdev Roman")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          selected.isDefined,
          selected.get.id == inserted.id
        )
      },
      test("#update author") {
        val uuid = UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")
        val author = Author(Some(uuid), "Martin Odersky", Some("Switzerland"))
        for {
          updated <- AuthorService.update(author)
          selected <- AuthorService.findByName("Martin Odersky")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          selected.isDefined,
          selected.get.country.contains("Switzerland")
        )
      },
      test("#delete author") {
        for {
          author <- AuthorService.findByName("Gvozdev Roman")
          _ <- Console.printLine(author)
          _ <- AuthorService.delete(author.get.id.map(_.toString).get)
          deleted <- AuthorService.findByName("Gvozdev Roman")
        } yield assertTrue(
          author.isDefined,
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Author repository/service")(
      authorRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.authorRepoLayer
      )

end AuthorRepositoryTest
