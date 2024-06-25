package libra

import libra.config.SecurityConfig
import zio.*
import zio.config.*
import zio.config.magnolia.*

object TestSecurityConfig:

  val source: ConfigProvider = ConfigProvider.fromMap(Map("secret" -> "123"))

  val securityConfig: ZLayer[Any, Config.Error, SecurityConfig] =
    ZLayer.fromZIO(source.load(deriveConfig[SecurityConfig]))

end TestSecurityConfig
