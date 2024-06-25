package libra.services.author

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.entities.Author
import libra.repositories.author.AuthorRepository
import libra.services.*
import libra.utils.ServiceError.*
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*

import java.util.UUID

object AuthorServiceTest extends ZIOSpecDefault:

  private def authorServiceSpec =
    suite("Author service & repo functions")(
      test("#all should return 3 authors") {
        for {
          authors <- AuthorService.all
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
        } yield assertTrue(
          author.name == "Donald Knuth"
        )
      },
      test("#findById should fail if the author does not exist") {
        for {
          result <- AuthorService
            .findById(
              "37d706ed-9591-4fd3-8811-9970194347da"
            )
            .exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#findByName should return the author if it exists") {
        for {
          authors <- AuthorService.findByName("Martin Odersky")
        } yield assertTrue(
          authors.nonEmpty,
          authors.head.id.contains(
            UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")
          )
        )
      },
      test("#findByName should fail if the author does not exist") {
        for {
          result <- AuthorService.findByName("Frank Herbert").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#create author is correct") {
        val author = Author(None, "Gvozdev Roman", Some("Russia"))
        for {
          inserted <- AuthorService.create(author)
          selected <- AuthorService.findByName("Gvozdev Roman")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.id == inserted.id
        )
      },
      test("#update author is correct") {
        val uuid   = UUID.fromString("7a7713e0-a518-4e3a-bf8f-bc984150a3b4")
        val author = Author(Some(uuid), "Martin Odersky", Some("Switzerland"))
        for {
          updated  <- AuthorService.update(author)
          selected <- AuthorService.findByName("Martin Odersky")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.country.contains("Switzerland")
        )
      },
      test("#delete author is correct") {
        for {
          authors <- AuthorService.findByName("Gvozdev Roman")
          _       <- AuthorService.delete(authors.head.id.map(_.toString).get)
          result  <- AuthorService.findByName("Gvozdev Roman").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Author service & repo tests")(
      authorServiceSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        TestRepoLayers.authorRepoLayer
      )

end AuthorServiceTest
