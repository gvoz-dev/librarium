package itcube.repositories.comment

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import itcube.LibraPostgresContainer
import itcube.entities.Comment
import itcube.repositories.RepoLayers
import zio.*
import zio.test.*
import zio.test.TestAspect.*

import java.util.UUID

object CommentRepositoryTest extends ZIOSpecDefault:

  private def commentRepoSpec: Spec[CommentRepository, Throwable] =
    suite("Comment repository CRUD functions")(
      test("#findByUser should return 1 comment") {
        for {
          comments <- CommentRepository.findByUser(
            "ca3e509d-06cf-4655-802a-7f8355339e2c"
          )
          _ <- Console.printLine(comments)
        } yield assertTrue(
          comments.size == 1,
          comments.head.text == "Отличная книга, рекомендую!"
        )
      },
      test("#findById should return nil if no comments exist") {
        for {
          comments <- CommentRepository.findByUser(
            "37d706ed-9591-4fd3-8811-9970194347da"
          )
          _ <- Console.printLine(comments)
        } yield assertTrue(
          comments.isEmpty
        )
      },
      test("#findByBook should return 1 comment") {
        for {
          comments <- CommentRepository.findByBook(
            "b43e5b87-a042-461b-8728-653eddced002"
          )
          _ <- Console.printLine(comments)
        } yield assertTrue(
          comments.size == 1,
          comments.head.text == "Отличная книга, рекомендую!"
        )
      },
      test("#findByBook should return nil if no comments exist") {
        for {
          comments <- CommentRepository.findByBook(
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          _ <- Console.printLine(comments)
        } yield assertTrue(
          comments.isEmpty
        )
      },
      test("#findByUserAndBook should return 1 comment") {
        for {
          comments <- CommentRepository.findByUserAndBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
          _ <- Console.printLine(comments)
        } yield assertTrue(
          comments.size == 1,
          comments.head.text == "Отличная книга, рекомендую!"
        )
      },
      test("#findByUserAndBook should return nil if no comments exist") {
        for {
          comments <- CommentRepository.findByUserAndBook(
            "ea962bb3-8f66-4256-bea5-8851c8f37dfb",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          _ <- Console.printLine(comments)
        } yield assertTrue(
          comments.isEmpty
        )
      },
      test("#create comment") {
        val comment = Comment(None, "test comment", true, None)
        for {
          inserted <- CommentRepository.create(
            comment,
            "ea962bb3-8f66-4256-bea5-8851c8f37dfb",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          _ <- Console.printLine(inserted)
          selected <- CommentRepository.findByUserAndBook(
            "ea962bb3-8f66-4256-bea5-8851c8f37dfb",
            "eb98fd47-793e-448c-ad50-0a68d1f76252"
          )
          _ <- Console.printLine(selected)
        } yield assertTrue(
          inserted.isDefined,
          selected.size == 1,
          selected.head.text == "test comment"
        )
      },
      test("#update comment") {
        val uuid = UUID.fromString("3bde6a1f-3eb7-4f36-b4c0-422ab4f39e99")
        val comment = Comment(Some(uuid), "test", false, None)
        for {
          updated <- CommentRepository.update(comment)
          selected <- CommentRepository.findByUserAndBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
          _ <- Console.printLine(selected)
        } yield assertTrue(
          updated.isDefined,
          selected.nonEmpty,
          selected.head.text == "test"
        )
      },
      test("#delete comment") {
        for {
          _ <- CommentRepository.delete("3bde6a1f-3eb7-4f36-b4c0-422ab4f39e99")
          deleted <- CommentRepository.findByUserAndBook(
            "ca3e509d-06cf-4655-802a-7f8355339e2c",
            "b43e5b87-a042-461b-8728-653eddced002"
          )
        } yield assertTrue(
          deleted.isEmpty
        )
      }
    )

  override def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Comment repository")(
      commentRepoSpec
    ) @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        RepoLayers.commentRepoLayer
      )

end CommentRepositoryTest
