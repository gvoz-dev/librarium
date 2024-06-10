package itcube.repositories.publisher

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.entities.Publisher
import itcube.repositories.RepoLayers
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object PublisherRepositoryTest extends ZIOSpecDefault:

  private def publisherRepoSpec: Spec[PublisherRepository, Throwable] =
    suite("Publisher repository CRUD functions")(
      test("#all should return 3 publishers") {
        for {
          publishers <- PublisherRepository.all
          _ <- Console.printLine(publishers)
        } yield assertTrue(
          publishers.nonEmpty,
          publishers.size == 3
        )
      },
      test("#findById should return the publisher if it exists") {
        for {
          publisher <- PublisherRepository.findById(
            "b43e5b87-a042-461b-8728-653eddced002"
          )
          _ <- Console.printLine(publisher)
        } yield assertTrue(
          publisher.isDefined,
          publisher.get.name == "Addison-Wesley"
        )
      },
      test("#findById should return none if the publisher does not exist") {
        for {
          publisher <- PublisherRepository.findById(
            "7a7713e0-a518-4e3a-bf8f-bc984150a3b4"
          )
          _ <- Console.printLine(publisher)
        } yield assertTrue(
          publisher.isEmpty
        )
      },
      test("#findByName should return the publisher if it exists") {
        for {
          publisher <- PublisherRepository.findByName("Artima")
          _ <- Console.printLine(publisher)
        } yield assertTrue(
          publisher.isDefined,
          publisher.get.id.contains(
            UUID.fromString("37d706ed-9591-4fd3-8811-9970194347da")
          )
        )
      },
      test("#findByName should return none if the publisher does not exist") {
        for {
          publisher <- PublisherRepository.findByName("Manning")
          _ <- Console.printLine(publisher)
        } yield assertTrue(
          publisher.isEmpty
        )
      },
      test("#create publisher") {
        val publisher = Publisher(None, "Manning", "USA")
        for {
          inserted <- PublisherRepository.create(publisher)
          _ <- Console.printLine(inserted)
          selected <- PublisherRepository.findByName("Manning")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          inserted.isDefined,
          selected.isDefined,
          inserted.get.id == selected.get.id
        )
      },
      test("#update publisher") {
        val uuid = UUID.fromString("4c007df8-4c12-435b-9c1d-082e204db21e")
        val publisher = Publisher(Some(uuid), "Science", "USSR")
        for {
          updated <- PublisherRepository.update(publisher)
          selected <- PublisherRepository.findByName("Science")
          _ <- Console.printLine(selected)
        } yield assertTrue(
          updated.isDefined,
          selected.isDefined,
          selected.get.name == "Science"
        )
      },
      test("#delete publisher") {
        for {
          publisher <- PublisherRepository.findByName("Manning")
          _ <- Console.printLine(publisher)
          _ <- PublisherRepository.delete(publisher.get.id.map(_.toString).get)
          deleted <- PublisherRepository.findByName("Manning")
        } yield assertTrue(
          publisher.isDefined,
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Publisher repository")(
      publisherRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.publisherRepoLayer
      )

end PublisherRepositoryTest
