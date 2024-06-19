package libra

import io.github.scottweaver.models.JdbcInfo
import libra.repositories.author.PgAuthorRepository
import libra.repositories.book.PgBookRepository
import libra.repositories.comment.PgCommentRepository
import libra.repositories.publisher.PgPublisherRepository
import libra.repositories.user.PgUserRepository
import libra.repositories.userbook.PgUserBookRepository
import zio.*

object TestRepoLayers:

  val publisherRepoLayer: ZLayer[JdbcInfo, Throwable, PgPublisherRepository] =
    DataSourceFromJdbcInfo.live >>>
      ZLayer.fromFunction(new PgPublisherRepository(_))

  val authorRepoLayer: ZLayer[JdbcInfo, Throwable, PgAuthorRepository] =
    DataSourceFromJdbcInfo.live >>>
      ZLayer.fromFunction(new PgAuthorRepository(_))

  val bookRepoLayer: ZLayer[JdbcInfo, Throwable, PgBookRepository] =
    DataSourceFromJdbcInfo.live >>>
      ZLayer.fromFunction(new PgBookRepository(_))

  val userRepoLayer: ZLayer[JdbcInfo, Throwable, PgUserRepository] =
    DataSourceFromJdbcInfo.live >>>
      ZLayer.fromFunction(new PgUserRepository(_))

  val userBookRepoLayer: ZLayer[JdbcInfo, Throwable, PgUserBookRepository] =
    DataSourceFromJdbcInfo.live >>>
      ZLayer.fromFunction(new PgUserBookRepository(_))

  val commentRepoLayer: ZLayer[JdbcInfo, Throwable, PgCommentRepository] =
    DataSourceFromJdbcInfo.live >>>
      ZLayer.fromFunction(new PgCommentRepository(_))

end TestRepoLayers
