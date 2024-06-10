package itcube

import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import zio.*

object LibraPostgresContainer:

  val live: ULayer[ZPostgreSQLContainer.Settings] =
    ZLayer.succeed(
      ZPostgreSQLContainer.Settings(
        "16.3-bookworm",
        "libra-test",
        "postgres",
        "12345"
      )
    )

end LibraPostgresContainer
