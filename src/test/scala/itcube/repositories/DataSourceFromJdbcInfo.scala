package itcube.repositories

import io.github.scottweaver.models.JdbcInfo
import org.postgresql.ds.PGSimpleDataSource
import zio.*

object DataSourceFromJdbcInfo:

  val live: ZLayer[JdbcInfo, Throwable, PGSimpleDataSource] =
    ZLayer.fromZIO {
      ZIO.serviceWithZIO[JdbcInfo] { jdbcInfo =>
        ZIO.attempt {
          val ds = new PGSimpleDataSource()
          ds.setURL(jdbcInfo.jdbcUrl)
          ds.setUser(jdbcInfo.username)
          ds.setPassword(jdbcInfo.password)
          ds
        }
      }
    }

end DataSourceFromJdbcInfo
