package example

import codionics.config._
import cats.effect.{IO, IOApp, Sync}
import scala.io.StdIn
import cats.effect.unsafe.implicits._
import cats.effect.ExitCode
import codionics.db.SessionBuilder
import cats.syntax.all._
import cats.effect.unsafe.implicits._
import codionics.db.PlayerRepository
import codionics.db.TypeValUtils._
import cats.effect.Ref
import com.datastax.oss.driver.api.core.CqlSession

object Hello extends Greeting with App {
  println(greeting)

  // val config = new ConfigReaderImpl().load().unsafeRunSync()
  // println(s"config: $config")

  val playerRepo = new PlayerRepository()
  val sessionIO  = getSession

  val program = for {
    session <- sessionIO
    _       <- getAllRowsIO(session)
    _       <- getByPkIO(session)
    _       <- getCountIO(session)
    _       <- cleanupSession
  } yield ()

  // val allRowsIO = getAllRowsIO(sessionIO)
  // val byPkRowIO = getByPkIO(sessionIO)
  // val countIO   = getCountIO(sessionIO)

  // allRowsIO.unsafeRunSync()
  // byPkRowIO.unsafeRunSync()
  // countIO.unsafeRunSync()

  // cleanupSession.unsafeRunSync()
  program.unsafeRunSync()

  def getAllRowsIO(session: CqlSession): IO[Seq[Map[String, Any]]] = {
    for {
      allRowsTV <- playerRepo.getAll(session)
      allRows    = allRowsTV.map(_.toNameValueMap)
      _         <- IO(println(s"all rows: ${allRows}"))
    } yield allRows
  }

  def getByPkIO(session: CqlSession): IO[Option[Map[String, Any]]] = {
    for {
      byPkRowTV <- playerRepo.getByPK("fedex")(session)
      singleRow  = byPkRowTV.map(_.toNameValueMap)
      _         <- IO(println(s"single row: ${singleRow}"))
    } yield singleRow
  }

  def getCountIO(session: CqlSession): IO[Long] = {
    for {
      count <- playerRepo.getCount(session)
      _     <- IO(println(s"count: ${count}"))
    } yield count
  }

  def getSession: IO[CqlSession] = {
    for {
      config       <- new ConfigReaderImpl().load()
      sessionRef   <- SessionBuilder.getSession().run(config)
      maybeSession <- sessionRef.get
    } yield maybeSession.get
  }

  def cleanupSession: IO[Unit] = {
    for {
      _ <- IO(println("cleaning up session"))
      _ <- SessionBuilder.cleanupSession()
    } yield ()
  }
}

trait Greeting {
  lazy val greeting: String = "hello"
}
