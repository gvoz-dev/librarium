ThisBuild / version := "1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.4.2"

ThisBuild / testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
ThisBuild / Test / fork := true

lazy val root = (project in file("."))
  .settings(
    name := "librarium"
  )

libraryDependencies += "dev.zio" %% "zio" % "2.1.1"
libraryDependencies += "dev.zio" %% "zio-json" % "0.6.2"
libraryDependencies += "dev.zio" %% "zio-http" % "3.0.0-RC8"

libraryDependencies += "dev.zio" %% "zio-config" % "4.0.2"
libraryDependencies += "dev.zio" %% "zio-config-typesafe" % "4.0.2"
libraryDependencies += "dev.zio" %% "zio-config-magnolia" % "4.0.2"

libraryDependencies += "dev.zio" %% "zio-logging" % "2.3.0"
libraryDependencies += "dev.zio" %% "zio-logging-slf4j" % "2.3.0"
libraryDependencies += "org.slf4j" % "slf4j-api" % "2.0.13"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.5.6"

libraryDependencies += "io.getquill" %% "quill-zio" % "4.8.5"
libraryDependencies += "io.getquill" %% "quill-jdbc-zio" % "4.8.5"

libraryDependencies += "org.postgresql" % "postgresql" % "42.7.3"

libraryDependencies += "dev.zio" %% "zio-test" % "2.1.2" % Test
libraryDependencies += "dev.zio" %% "zio-test-sbt" % "2.1.2" % Test

libraryDependencies += "org.flywaydb" % "flyway-database-postgresql" % "10.15.0" % Test
libraryDependencies += "io.github.scottweaver" %% "zio-2-0-testcontainers-postgresql" % "0.10.0" % Test
libraryDependencies += "io.github.scottweaver" %% "zio-2-0-db-migration-aspect" % "0.10.0" % Test
