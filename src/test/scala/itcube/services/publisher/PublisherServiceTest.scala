package itcube.services.publisher

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.*
import itcube.entities.Publisher
import itcube.repositories.publisher.PublisherRepository
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object PublisherServiceTest extends ZIOSpecDefault:

  private def publisherServiceSpec =
    suite("Publisher service & repo functions")(
      test("#all should return 3 publishers") {
        for {
          publishers <- PublisherService.all
        } yield assertTrue(
          publishers.nonEmpty,
          publishers.size == 3
        )
      },
      test("#findById should return the publisher if it exists") {
        for {
          publisher <- PublisherService.findById(
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          publisher.isDefined,
          publisher.get.name == "Addison-Wesley"
        )
      },
      test("#findById should return none if the publisher does not exist") {
        for {
          publisher <- PublisherService.findById(
            "7a7713e0-a518-4e3a-bf8f-bc984150a3b4"
          )
        } yield assertTrue(
          publisher.isEmpty
        )
      },
      test("#findByName should return the publisher if it exists") {
        for {
          publisher <- PublisherService.findByName("Artima")
        } yield assertTrue(
          publisher.isDefined,
          publisher.get.id.contains(
            UUID.fromString("37d706ed-9591-4fd3-8811-9970194347da")
          )
        )
      },
      test("#findByName should return none if the publisher does not exist") {
        for {
          publisher <- PublisherService.findByName("Manning")
        } yield assertTrue(
          publisher.isEmpty
        )
      },
      test("#create publisher is correct") {
        val publisher = Publisher(None, "Manning", "USA")
        for {
          inserted <- PublisherService.create(publisher)
          selected <- PublisherService.findByName("Manning")
        } yield assertTrue(
          selected.isDefined,
          selected.get.id == inserted.id
        )
      },
      test("#update publisher is correct") {
        val uuid = UUID.fromString("4c007df8-4c12-435b-9c1d-082e204db21e")
        val publisher = Publisher(Some(uuid), "Science", "USSR")
        for {
          updated <- PublisherService.update(publisher)
          selected <- PublisherService.findByName("Science")
        } yield assertTrue(
          selected.isDefined,
          selected.get.name == "Science"
        )
      },
      test("#delete publisher is correct") {
        for {
          publisher <- PublisherService.findByName("Manning")
          _ <- PublisherService.delete(publisher.get.id.map(_.toString).get)
          deleted <- PublisherService.findByName("Manning")
        } yield assertTrue(
          publisher.isDefined,
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Publisher service & repo tests")(
      publisherServiceSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        TestRepoLayers.publisherRepoLayer
      )

end PublisherServiceTest
