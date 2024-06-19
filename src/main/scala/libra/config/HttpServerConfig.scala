package libra.config

import zio.Config
import zio.config.magnolia.deriveConfig

/** Конфигурация HTTP-сервера.
  *
  * @param host
  *   хост
  * @param port
  *   порт
  * @param nThreads
  *   количество потоков Netty
  */
case class HttpServerConfig(
    host: String,
    port: Int,
    nThreads: Int
)

object HttpServerConfig:

  /** Автоматический вывод конфигурации HTTP-сервера. */
  val config: Config[HttpServerConfig] =
    deriveConfig[HttpServerConfig].nested("HttpServerConfig")

end HttpServerConfig
