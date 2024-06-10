package itcube

import itcube.config.HttpServerConfig
import itcube.repositories.author.PgAuthorRepository
import itcube.repositories.book.PgBookRepository
import itcube.repositories.comment.PgCommentRepository
import itcube.repositories.publisher.PgPublisherRepository
import itcube.repositories.user.PgUserRepository
import itcube.repositories.userbook.PgUserBookRepository
import itcube.rest.api.{AuthorRoutes, BookRoutes, PublisherRoutes, UserRoutes}
import zio.*
import zio.config.typesafe.FromConfigSourceTypesafe
import zio.http.*
import zio.http.Middleware.*
import zio.http.netty.NettyConfig
import zio.logging.backend.SLF4J

/** Бэкенд веб-приложения для читателей "Librarium". Точка входа в
  * ZIO-приложение.
  *
  * @author
  *   github.com/gvoz-dev
  */
object App extends ZIOAppDefault:

  /** Переопределённый слой начальной загрузки. */
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.removeDefaultLoggers >>>
      Runtime.setConfigProvider(ConfigProvider.fromResourcePath()) >>>
      SLF4J.slf4j

  /** Слой конфигурации сервера. */
  private val serverConfig: ZLayer[Any, Config.Error, Server.Config] =
    ZLayer.fromZIO(
      ZIO.config[HttpServerConfig](HttpServerConfig.config) map { conf =>
        Server.Config.default.binding(conf.host, conf.port)
      }
    )

  /** Слой конфигурации Netty. */
  private val nettyConfig: ZLayer[Any, Config.Error, NettyConfig] =
    ZLayer.fromZIO(
      ZIO.config[HttpServerConfig](HttpServerConfig.config) map { conf =>
        NettyConfig.default.maxThreads(conf.nThreads)
      }
    )

  /** Конфигурация CORS. */
  private val corsConfig: CorsConfig = CorsConfig()

  /** Конкатенация [[Routes]]. */
  private val routes =
    AuthorRoutes() ++ PublisherRoutes() ++ BookRoutes() ++ UserRoutes()

  /** Запуск ZIO-приложения. */
  def run: ZIO[Scope, Any, Any] =
    for {
      _ <- ZIO.logInfo("Starting Librarium server")
      _ <- Server
        .serve(routes @@ cors(corsConfig))
        .provide(
          PgPublisherRepository.live,
          PgAuthorRepository.live,
          PgBookRepository.live,
          PgUserRepository.live,
          PgUserBookRepository.live,
          PgCommentRepository.live,
          serverConfig,
          Server.live
        )
    } yield ()
  end run

end App
