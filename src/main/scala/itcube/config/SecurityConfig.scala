package itcube.config

import zio.Config
import zio.config.magnolia.deriveConfig

/** Конфигурация безопасности.
  *
  * @param secret
  *   секретный ключ
  */
case class SecurityConfig(
    secret: String
)

object SecurityConfig:

  /** Автоматический вывод конфигурации безопасности. */
  val config: Config[SecurityConfig] =
    deriveConfig[SecurityConfig].nested("SecurityConfig")

end SecurityConfig
