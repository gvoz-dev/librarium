package libra.services.book

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.entities.Book
import libra.repositories.book.BookRepository
import libra.utils.ServiceError.*
import zio.*
import zio.test.*
import zio.test.Assertion.*
import zio.test.TestAspect.*

import java.util.UUID

object BookServiceTest extends ZIOSpecDefault:

  private def bookServiceSpec =
    suite("Book service & repo functions")(
      test("#all should return 2 books") {
        for {
          books <- BookService.all
        } yield assertTrue(
          books.nonEmpty,
          books.size == 2
        )
      },
      test("#findById should return the book if it exists") {
        for {
          book <- BookService.findById(
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          book.title == "Scala. Профессиональное программирование"
        )
      },
      test("#findById should fail if the book does not exist") {
        for {
          result <- BookService
            .findById(
              "7a7713e0-a518-4e3a-bf8f-bc984150a3b4"
            )
            .exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#findByTitle should return the book if it exists") {
        for {
          books <- BookService.findByTitle(
            "Scala. Профессиональное программирование"
          )
        } yield assertTrue(
          books.nonEmpty,
          books.head.id.contains(
            UUID.fromString("b43e5b87-a042-461b-8728-653eddced002")
          )
        )
      },
      test("#findByTitle should fail if the book does not exist") {
        for {
          result <- BookService.findByTitle("Dune").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#create book is correct") {
        val book = Book(
          None,
          "Dune",
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None,
          None
        )
        for {
          inserted <- BookService.create(book)
          selected <- BookService.findByTitle("Dune")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.id == inserted.id
        )
      },
      test("#update book is correct") {
        for {
          books <- BookService.findByTitle("Dune")
          updated <- BookService.update(
            books.head.copy(language = Some("EN"))
          )
          selected <- BookService.findByTitle("Dune")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.language.contains("EN")
        )
      },
      test("#delete book is correct") {
        for {
          book <- BookService.findByTitle("Dune")
          _ <- BookService.delete(book.head.id.map(_.toString).get)
          result <- BookService.findByTitle("Dune").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Book service & repo tests")(
      bookServiceSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        TestRepoLayers.bookRepoLayer
      )

end BookServiceTest
