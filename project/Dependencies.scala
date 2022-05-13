import sbt._

object Dependencies {

  val catsVersion     = "2.7.0"
  lazy val catsCore   = "org.typelevel" %% "cats-core"   % catsVersion
  lazy val catsEffect = "org.typelevel" %% "cats-effect" % "3.3.11"

  val pureConfigVersion         = "0.17.1"
  lazy val pureConfig           = "com.github.pureconfig" %% "pureconfig"             % pureConfigVersion
  lazy val pureConfigCatsEffect = "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfigVersion

  val cassandraDriverVersion     = "4.14.0"
  lazy val cassandraDriver       = "com.datastax.oss" % "java-driver-core"          % cassandraDriverVersion
  lazy val cassandraQueryBuilder = "com.datastax.oss" % "java-driver-query-builder" % cassandraDriverVersion

  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.2.11"
}
