package libra.rest.api.login

import io.github.scottweaver.zio.aspect.DbMigrationAspect
import io.github.scottweaver.zio.testcontainers.postgres.ZPostgreSQLContainer
import libra.*
import libra.rest.api.{*, given}
import libra.utils.*
import zio.*
import zio.http.*
import zio.http.netty.NettyConfig
import zio.http.netty.server.NettyDriver
import zio.schema.codec.JsonCodec.schemaBasedBinaryCodec
import zio.test.*
import zio.test.TestAspect.sequential

object LoginTest extends ZIOSpecDefault:

  def spec: Spec[TestEnvironment & Scope, Any] =
    (suite("Login route tests") {
      test("#auth is correct") {
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
          token    <- response.body.to[Token]
          secret   <- Security.secret
          payload  <- JsonWebToken.validateJwt(token.jwt, secret)
        } yield assertTrue(
          response.status == Status.Ok,
          payload.content.role == "admin"
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
        TestSecurityConfig.live
      )

end LoginTest
