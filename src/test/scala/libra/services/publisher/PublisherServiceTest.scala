package libra.services.publisher

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.entities.Publisher
import libra.repositories.publisher.PublisherRepository
import libra.utils.ServiceError.*
import zio.*
import zio.test.*
import zio.test.Assertion.*
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
          publisher.name == "Addison-Wesley"
        )
      },
      test("#findById should fail if the publisher does not exist") {
        for {
          result <- PublisherService
            .findById(
              "7a7713e0-a518-4e3a-bf8f-bc984150a3b4"
            )
            .exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#findByName should return the publisher if it exists") {
        for {
          publishers <- PublisherService.findByName("Artima")
        } yield assertTrue(
          publishers.nonEmpty,
          publishers.head.id.contains(
            UUID.fromString("37d706ed-9591-4fd3-8811-9970194347da")
          )
        )
      },
      test("#findByName should fail if the publisher does not exist") {
        for {
          result <- PublisherService.findByName("Manning").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
      },
      test("#create publisher is correct") {
        val publisher = Publisher(None, "Manning", "USA")
        for {
          inserted <- PublisherService.create(publisher)
          selected <- PublisherService.findByName("Manning")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.id == inserted.id
        )
      },
      test("#update publisher is correct") {
        val uuid = UUID.fromString("4c007df8-4c12-435b-9c1d-082e204db21e")
        val publisher = Publisher(Some(uuid), "Science", "USSR")
        for {
          updated <- PublisherService.update(publisher)
          selected <- PublisherService.findByName("Science")
        } yield assertTrue(
          selected.nonEmpty,
          selected.head.name == "Science"
        )
      },
      test("#delete publisher is correct") {
        for {
          publisher <- PublisherService.findByName("Manning")
          _ <- PublisherService.delete(publisher.head.id.map(_.toString).get)
          result <- PublisherService.findByName("Manning").exit
        } yield assert(result)(fails(isSubtype[NotFound](anything)))
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
