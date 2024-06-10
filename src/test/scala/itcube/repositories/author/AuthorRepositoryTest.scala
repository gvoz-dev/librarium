package itcube.repositories.author

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.entities.Author
import itcube.repositories.RepoLayers
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object AuthorRepositoryTest extends ZIOSpecDefault:

  private def authorRepoSpec: Spec[AuthorRepository, Throwable] =
    suite("Author repository CRUD functions")(
      test("#all should return 3 authors") {
        for {
          authors <- AuthorRepository.all
          _ <- Console.printLine(authors)
        } yield assertTrue(
          authors.nonEmpty,
          authors.size == 3
        )
      },
      test("#findById should return the author if it exists") {
        for {
          author <- AuthorRepository.findById(
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
          author <- AuthorRepository.findById(
            "37d706ed-9591-4fd3-8811-9970194347da"
          )
          _ <- Console.printLine(author)
        } yield assertTrue(
          author.isEmpty
        )
      },
      test("#findByName should return the author if it exists") {
        for {
          author <- AuthorRepository.findByName("Martin Odersky")
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
          author <- AuthorRepository.findByName("Frank Herbert")
          _ <- Console.printLine(author)
        } yield assertTrue(
          author.isEmpty
        )
      },
      test("#create author") {
        val author = Author(None, "Gvozdev Roman", Some("Russia"))
        for {
          inserted <- AuthorRepository.create(author)
          _ <- Console.printLine(inserted)
          selected <- AuthorRepository.findByName("Gvozdev Roman")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          inserted.isDefined,
          selected.isDefined,
          inserted.get.id == selected.get.id
        )
      },
      test("#update author") {
        val uuid = UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")
        val author = Author(Some(uuid), "Martin Odersky", Some("Switzerland"))
        for {
          updated <- AuthorRepository.update(author)
          selected <- AuthorRepository.findByName("Martin Odersky")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          updated.isDefined,
          selected.isDefined,
          selected.get.country.contains("Switzerland")
        )
      },
      test("#delete author") {
        for {
          author <- AuthorRepository.findByName("Gvozdev Roman")
          _ <- Console.printLine(author)
          _ <- AuthorRepository.delete(author.get.id.map(_.toString).get)
          deleted <- AuthorRepository.findByName("Gvozdev Roman")
        } yield assertTrue(
          author.isDefined,
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Author repository")(
      authorRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.authorRepoLayer
      )

end AuthorRepositoryTest
