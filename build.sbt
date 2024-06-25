ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
ThisBuild / Test / fork := true

lazy val root = (project in file("."))
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)
  .settings(
    name := "librarium"
  )

libraryDependencies += "dev.zio" %% "zio"      % "2.1.4"
libraryDependencies += "dev.zio" %% "zio-json" % "0.7.0"

val zioTestVersion = "2.1.4"
libraryDependencies += "dev.zio" %% "zio-test"     % zioTestVersion % Test
libraryDependencies += "dev.zio" %% "zio-test-sbt" % zioTestVersion % Test

val zioHttpVersion = "3.0.0-RC8"
libraryDependencies += "dev.zio" %% "zio-http"         % zioHttpVersion
libraryDependencies += "dev.zio" %% "zio-http-testkit" % zioHttpVersion % Test

val zioConfigVersion = "4.0.2"
libraryDependencies += "dev.zio" %% "zio-config"          % zioConfigVersion
libraryDependencies += "dev.zio" %% "zio-config-typesafe" % zioConfigVersion
libraryDependencies += "dev.zio" %% "zio-config-magnolia" % zioConfigVersion

val zioLoggingVersion = "2.3.0"
libraryDependencies += "dev.zio"       %% "zio-logging"       % zioLoggingVersion
libraryDependencies += "dev.zio"       %% "zio-logging-slf4j" % zioLoggingVersion
libraryDependencies += "org.slf4j"      % "slf4j-api"         % "2.0.13"
libraryDependencies += "ch.qos.logback" % "logback-classic"   % "1.5.6"

val quillVersion = "4.8.5"
libraryDependencies += "io.getquill"   %% "quill-zio"      % quillVersion
libraryDependencies += "io.getquill"   %% "quill-jdbc-zio" % quillVersion
libraryDependencies += "org.postgresql" % "postgresql"     % "42.7.3"

val flywayVersion = "10.15.0"
libraryDependencies += "org.flywaydb" % "flyway-core"                % flywayVersion
libraryDependencies += "org.flywaydb" % "flyway-database-postgresql" % flywayVersion

val zioTestcontainersVersion = "0.10.0"
libraryDependencies += "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % zioTestcontainersVersion % Test
libraryDependencies += "io.github.scottweaver" %% "zio-2-0-db-migration-aspect"       % zioTestcontainersVersion % Test

libraryDependencies += "io.taig" %% "taigless-jwt-pdi" % "0.15.0"

// SBT Native Packager
Docker / packageName := "libra-backend"
Docker / version     := version.value
dockerBaseImage      := "eclipse-temurin:21"
dockerExposedPorts   := Seq(8080)
