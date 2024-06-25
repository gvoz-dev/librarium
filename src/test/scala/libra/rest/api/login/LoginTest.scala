package libra.rest.api.login

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.rest.api.{*, given}
import zio.*
import zio.http.*
import zio.http.netty.NettyConfig
import zio.http.netty.server.NettyDriver
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.test.*
import zio.test.TestAspect.sequential

object LoginTest extends ZIOSpecDefault:

  def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Login endpoint") {
      test("Test auth") {
        for {
          client <- ZIO.service[Client]
          port   <- ZIO.serviceWith[Server](_.port)
          testRequest = Request
            .post(
              url = URL.root.port(port) / "api" / "v1" / "login",
              body = Body.from(libra.rest.api.Credentials("admin@example.com", "12345"))
            )
            .addHeaders(Headers(Header.ContentType(MediaType.application.json)))
          _        <- TestServer.addRoute(Login.route)
          response <- client(testRequest)
          body     <- response.body.asString
        } yield assertTrue(
          response.status == Status.Ok,
          body.contains("jwt")
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
        TestRepoLayers.userRepoLayer,
        TestSecurityConfig.securityConfig
      )

end LoginTest
