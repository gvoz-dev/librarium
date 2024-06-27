package libra.rest.api.user

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.entities.User
import zio.*
import zio.http.*
import zio.http.netty.NettyConfig
import zio.http.netty.server.NettyDriver
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.test.*
import zio.test.TestAspect.sequential

object RegistrationTest extends ZIOSpecDefault:

  def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Registration route tests") {
      test("#registration is correct") {
        for {
          client <- ZIO.service[Client]
          port   <- ZIO.serviceWith[Server](_.port)
          testRequest = Request
            .post(
              url = URL.root.port(port) / "api" / "v1" / "registration",
              body = Body.from(User(None, "Houdini", "houdini@example.com", "AllToScala"))
            )
            .addHeaders(Headers(Header.ContentType(MediaType.application.json)))
          _        <- TestServer.addRoute(Registration.route)
          response <- client(testRequest)
          user     <- response.body.to[User]
        } yield assertTrue(
          response.status == Status.Created,
          user.name == "Houdini",
          user.id.isDefined
        )
      }
    } @@ DbMigrationAspect.migrate("db/migration")() @@ sequential)
      .provideShared(
        TestServer.layer,
        Scope.default,
        ZLayer.succeed(Server.Config.default.onAnyOpenPort),
        Client.default,
        NettyDriver.customized,
        ZLayer.succeed(NettyConfig.defaultWithFastShutdown),
        LibraPostgresContainer.live,
        ZPostgreSQLContainer.live,
        TestRepoLayers.userRepoLayer
      )

end RegistrationTest
