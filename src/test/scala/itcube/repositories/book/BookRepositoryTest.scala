package itcube.repositories.book

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.entities.Book
import itcube.repositories.RepoLayers
import itcube.services.book.BookService
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object BookRepositoryTest extends ZIOSpecDefault:

  private def bookRepoSpec: Spec[BookRepository, Throwable] =
    suite("Book repository/service functions")(
      test("#all should return 2 books") {
        for {
          books <- BookService.all
          _ <- Console.printLine(books)
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
          _ <- Console.printLine(book)
        } yield assertTrue(
          book.isDefined,
          book.get.title == "Scala. Профессиональное программирование"
        )
      },
      test("#findById should return none if the book does not exist") {
        for {
          book <- BookService.findById(
            "7a7713e0-a518-4e3a-bf8f-bc984150a3b4"
          )
          _ <- Console.printLine(book)
        } yield assertTrue(
          book.isEmpty
        )
      },
      test("#findByTitle should return the book if it exists") {
        for {
          book <- BookService.findByTitle(
            "Scala. Профессиональное программирование"
          )
          _ <- Console.printLine(book)
        } yield assertTrue(
          book.isDefined,
          book.get.id.contains(
            UUID.fromString("b43e5b87-a042-461b-8728-653eddced002")
          )
        )
      },
      test("#findByTitle should return none if the book does not exist") {
        for {
          book <- BookService.findByTitle("Dune")
          _ <- Console.printLine(book)
        } yield assertTrue(
          book.isEmpty
        )
      },
      test("#create book") {
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
          _ <- Console.printLine(inserted)
          selected <- BookService.findByTitle("Dune")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          selected.isDefined,
          selected.get.id == inserted.id
        )
      },
      test("#update book") {
        for {
          book <- BookService.findByTitle("Dune")
          updated <- BookService.update(
            book.map(_.copy(language = Some("EN"))).get
          )
          selected <- BookService.findByTitle("Dune")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          selected.isDefined,
          selected.get.language.contains("EN")
        )
      },
      test("#delete book") {
        for {
          book <- BookService.findByTitle("Dune")
          _ <- Console.printLine(book)
          _ <- BookService.delete(book.get.id.map(_.toString).get)
          deleted <- BookService.findByTitle("Dune")
        } yield assertTrue(
          book.isDefined,
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Book repository/service")(
      bookRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.bookRepoLayer
      )

end BookRepositoryTest
