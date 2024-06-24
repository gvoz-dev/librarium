package libra

import libra.config.*
import libra.repositories.PostgresDataSource
import libra.repositories.author.PgAuthorRepository
import libra.repositories.book.PgBookRepository
import libra.repositories.publisher.PgPublisherRepository
import libra.repositories.user.PgUserRepository
import libra.repositories.userbook.PgUserBookRepository
import libra.rest.RestRoutes
import org.flywaydb.core.Flyway
import zio.*
import zio.config.typesafe.FromConfigSourceTypesafe
import zio.http.*
import zio.logging.backend.SLF4J

import javax.sql.DataSource
import scala.util.Try

/** Бэкенд веб-приложения для читателей "Librarium":
  *   - личная библиотека,
  *   - прогресс прочитанного,
  *   - рейтинг книг,
  *   - рецензии и комментарии.
  *
  * Точка входа в ZIO-приложение.
  *
  * @author
  *   github.com/gvoz-dev
  */
object App extends ZIOAppDefault:

  /** Переопределённый слой начальной загрузки ZIO-приложения. */
  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    Runtime.setConfigProvider(ConfigProvider.fromResourcePath()) >>>
      Runtime.removeDefaultLoggers >>> SLF4J.slf4j

  /** Слой Flyway для выполнения миграций БД. */
  private val flyway: ZLayer[Any, Throwable, Flyway] =
    PostgresDataSource.live >>>
      ZLayer.fromFunction((ds: DataSource) =>
        Flyway.configure().dataSource(ds).load()
      )

  /** Слой конфигурации безопасности. */
  private val securityConfig: ZLayer[Any, Config.Error, SecurityConfig] =
    ZLayer.fromZIO(ZIO.config[SecurityConfig](SecurityConfig.config))

  /** Слой конфигурации сервера. */
  private val serverConfig: ZLayer[Any, Config.Error, Server.Config] =
    ZLayer.fromZIO(
      ZIO.config[HttpServerConfig](HttpServerConfig.config) map { conf =>
        Server.Config.default.binding(conf.host, conf.port)
      }
    )

  /** Запуск ZIO-приложения. */
  def run: ZIO[Scope, Any, Any] =
    for {
      _ <- ZIO.logInfo("Start database migration")
      _ <- ZIO
        .serviceWithZIO[Flyway](fw => ZIO.fromTry(Try(fw.migrate())))
        .orElse(ZIO.logError("Flyway migration error"))
        .provide(flyway)
      _ <- ZIO.logInfo("Start server")
      _ <- Server
        .serve(RestRoutes())
        .provide(
          PgUserRepository.live,
          PgBookRepository.live,
          PgUserBookRepository.live,
          PgAuthorRepository.live,
          PgPublisherRepository.live,
          securityConfig,
          serverConfig,
          Server.live
        )
        .onExit(exit => ZIO.logInfo(s"Stop server"))
    } yield ()
  end run

end App
