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
import cats.effect.kernel.Resource

object Hello extends Greeting with App {
  println(greeting)

  // val config = new ConfigReaderImpl().load().unsafeRunSync()
  // println(s"config: $config")

  val playerRepo      = new PlayerRepository()
  val sessionResource = Resource.make(getSession)(_ => cleanupSession)

  sessionResource.use(getProgram).unsafeRunSync()

  def getProgram(session: CqlSession): IO[Unit] = {
    for {
      _ <- getAllRowsIO(session)
      _ <- getByPkIO(session)
      _ <- getCountIO(session)
      _ <- getByQueryIO(session)
      _ <- insertIO(session)
      _ <- deleteByPkIO(session)
    } yield ()
  }

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

  def getByQueryIO(session: CqlSession): IO[Seq[Map[String, Any]]] = {
    val query = "select * from players where city = 'Ajmer'"
    for {
      rowsByQueryTV <- playerRepo.getByQuery(query)(session)
      rowsByQuery    = rowsByQueryTV.map(_.toNameValueMap)
      _             <- IO(println(s"rows by query: ${rowsByQuery}"))
    } yield rowsByQuery
  }

  def deleteByPkIO(session: CqlSession): IO[Unit] = {
    for {
      _ <- playerRepo.delete("chintu1")(session)
    } yield ()
  }

  def insertIO(session: CqlSession): IO[Option[Map[String, Any]]] = {
    val data = Map(
      "nickname"   -> "chintu1",
      "first_name" -> "Rishi1",
      "last_name"  -> "Kapoor1",
      "city"       -> "Jodhpur1",
      "country"    -> "India1",
      "zip"        -> "305002"
    )

    for {
      insertedRowTV <- playerRepo.insert(data)(session)
      singleRow      = insertedRowTV.map(_.toNameValueMap)
      _             <- IO(println(s"inserted row: ${singleRow}"))
    } yield singleRow
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
