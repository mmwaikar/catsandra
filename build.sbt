import Dependencies._

ThisBuild / scalaVersion     := "2.13.8"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.codionics"
ThisBuild / organizationName := "codionics"

lazy val root = (project in file("."))
  .settings(
    name := "catsandra",
    libraryDependencies ++= Seq(
      catsCore,
      catsEffect,
      pureConfig,
      pureConfigCatsEffect,
      cassandraDriver,
      cassandraQueryBuilder,
      scalaTest % Test
    )
  )

// See https://www.scala-sbt.org/1.x/docs/Using-Sonatype.html for instructions on how to publish to Sonatype.
