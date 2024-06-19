package libra.services.comment

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.entities.Comment
import libra.repositories.comment.CommentRepository
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object CommentServiceTest extends ZIOSpecDefault:

  private def commentServiceSpec =
    suite("Comment service & repo functions")(
      test("#findById should return comment if it exists") {
        for {
          comment <- CommentService.findById(
            "3bde6a1f-3eb7-4f36-b4c0-422ab4f39e99"
          )
        } yield assertTrue(
          comment.isDefined,
          comment.exists(_.text == "Отличная книга, рекомендую!")
        )
      },
      test("#findByUser should return 1 comment") {
        for {
          comments <- CommentService.findByUser(
            "ca3e509d-06cf-4655-802a-7f8355339e2c"
          )
        } yield assertTrue(
          comments.size == 1,
          comments.head.text == "Отличная книга, рекомендую!"
        )
      },
      test("#findById should return none if no comments exist") {
        for {
          comments <- CommentService.findByUser(
            "37d706ed-9591-4fd3-8811-9970194347da"
          )
        } yield assertTrue(
          comments.isEmpty
        )
      },
      test("#findByBook should return 1 comment") {
        for {
          comments <- CommentService.findByBook(
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          comments.size == 1,
          comments.head.text == "Отличная книга, рекомендую!"
        )
      },
      test("#findByBook should return none if no comments exist") {
        for {
          comments <- CommentService.findByBook(
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
        } yield assertTrue(
          comments.isEmpty
        )
      },
      test("#findByUserAndBook should return 1 comment") {
        for {
          comments <- CommentService.findByUserAndBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          comments.size == 1,
          comments.head.text == "Отличная книга, рекомендую!"
        )
      },
      test("#findByUserAndBook should return none if no comments exist") {
        for {
          comments <- CommentService.findByUserAndBook(
            "ea962bb3-8f66-4256-bea5-8851c8f37dfb",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
        } yield assertTrue(
          comments.isEmpty
        )
      },
      test("#create comment is correct") {
        val comment = Comment(None, "test comment", true, None)
        for {
          inserted <- CommentService.create(
            comment,
            "ea962bb3-8f66-4256-bea5-8851c8f37dfb",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          selected <- CommentService.findByUserAndBook(
            "ea962bb3-8f66-4256-bea5-8851c8f37dfb",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
        } yield assertTrue(
          selected.size == 1,
          selected.head == inserted
        )
      },
      test("#update comment is correct") {
        val uuid = UUID.fromString("3bde6a1f-3eb7-4f36-b4c0-422ab4f39e99")
        val comment = Comment(Some(uuid), "test", false, None)
        for {
          updated <- CommentService.update(comment)
          selected <- CommentService.findByUserAndBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          selected.nonEmpty,
          selected.head == updated
        )
      },
      test("#delete comment is correct") {
        for {
          _ <- CommentService.delete("3bde6a1f-3eb7-4f36-b4c0-422ab4f39e99")
          deleted <- CommentService.findByUserAndBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Comment service & repo tests")(
      commentServiceSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        TestRepoLayers.commentRepoLayer
      )

end CommentServiceTest
