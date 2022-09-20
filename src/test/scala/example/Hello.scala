package example

import codionics.config._
import cats.effect.{IO, IOApp, Sync}
import scala.io.StdIn
import cats.effect.unsafe.implicits._
import cats.effect.ExitCode
import codionics.db.SessionBuilder
import cats.syntax.all._
import cats.effect.unsafe.implicits._
import codionics.db.PlayerTypeValRepository
import codionics.db.TypeValUtils._
import cats.effect.Ref
import com.datastax.oss.driver.api.core.CqlSession
import cats.effect.kernel.Resource
import codionics.db.SessionResource

object Hello extends App {

  // val config = new ConfigReaderImpl().load().unsafeRunSync()
  // println(s"config: $config")

  val playerRepo      = new PlayerTypeValRepository()
  val sessionResource = SessionResource.cqlSessionResource

  sessionResource.use(getProgram).unsafeRunSync()

  def getProgram(session: CqlSession): IO[Unit] = {
    for {
      _ <- getAllRowsIO(session)
      _ <- getByPkIO(session, "fedex")
      _ <- getCountIO(session)
      _ <- getByQueryIO(session)
      _ <- insertIO(session)
      _ <- getCountIO(session)
      _ <- getByPkIO(session, "chintu1")
      _ <- updateIO(session)
      _ <- getByPkIO(session, "chintu1")
      _ <- deleteByPkIO(session, "chintu1")
      _ <- getCountIO(session)
    } yield ()
  }

  def getAllRowsIO(session: CqlSession): IO[Seq[Map[String, Any]]] = {
    for {
      allRowsTV <- playerRepo.getAll(session)
      allRows    = allRowsTV.map(_.toNameValueMap)
      _         <- IO(println(s"all rows: ${allRows}"))
      _         <- IO(println(s"-----------------------------"))
    } yield allRows
  }

  def getByPkIO(session: CqlSession, pkValue: String): IO[Option[Map[String, Any]]] = {
    for {
      byPkRowTV <- playerRepo.getByPK(pkValue)(session)
      singleRow  = byPkRowTV.map(_.toNameValueMap)
      _         <- IO(println(s"single row: ${singleRow}"))
      _         <- IO(println(s"-----------------------------"))
    } yield singleRow
  }

  def getCountIO(session: CqlSession): IO[Long] = {
    for {
      count <- playerRepo.getCount(session)
      _     <- IO(println(s"count: ${count}"))
      _     <- IO(println(s"-----------------------------"))
    } yield count
  }

  def getByQueryIO(session: CqlSession): IO[Seq[Map[String, Any]]] = {
    val query = "select * from players where city = 'Ajmer'"
    for {
      rowsByQueryTV <- playerRepo.getByQuery(query)(session)
      rowsByQuery    = rowsByQueryTV.map(_.toNameValueMap)
      _             <- IO(println(s"rows by query: ${rowsByQuery}"))
      _             <- IO(println(s"-----------------------------"))
    } yield rowsByQuery
  }

  def deleteByPkIO(session: CqlSession, pkValue: String): IO[Unit] = {
    for {
      _ <- playerRepo.delete(pkValue)(session)
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
      _             <- IO(println(s"-----------------------------"))
    } yield singleRow
  }

  def updateIO(session: CqlSession): IO[Option[Map[String, Any]]] = {
    val data = Map(
      "nickname"   -> "chintu1",
      "first_name" -> "Rishi2",
      "last_name"  -> "Kapoor2",
      "city"       -> "Jodhpur2",
      "country"    -> "India2",
      "zip"        -> "305003"
    )

    for {
      updatedRowTV <- playerRepo.update(data)(session)
      singleRow     = updatedRowTV.map(_.toNameValueMap)
      _            <- IO(println(s"updated row: ${singleRow}"))
      _            <- IO(println(s"-----------------------------"))
    } yield singleRow
  }
}
