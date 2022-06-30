package codionics.db

import cats.data.Kleisli
import cats.effect.IO
import com.datastax.oss.driver.api.core.CqlSession
import scala.jdk.FutureConverters._
import codionics.domain._

abstract class AbstractGenericRepository[TPK] extends GenericRepository[TPK] {

  protected val ALLOW_FILTERING: String = "ALLOW FILTERING"

  override def getAll: Kleisli[IO, CqlSession, Seq[Map[String, Any]]] =
    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(selectAllQuery)
      rowsMap  = getRows(result)
    } yield rowsMap

  override def getByPK(pk: TPK): Kleisli[IO, CqlSession, Option[Map[String, Any]]] =
    for {
      session  <- Kleisli.ask[IO, CqlSession]
      statement = session.prepare(selectByPkColumnQuery)
      result    = session.execute(statement.bind(pk))
      rowsMap   = getRows(result)
    } yield rowsMap.headOption

  override def getCount: Kleisli[IO, CqlSession, Long] =
    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(selectCountQuery)
      rowMap   = getRow(result)
      _        = println(s"rowMap: $rowMap")
    } yield rowMap.getOrElse("count", TypeVal.DEFAULT_LONG.value).asInstanceOf[Long]

  override def getByQuery(query: String): Kleisli[IO, CqlSession, Seq[Map[String, Any]]] = {
    val updatedQuery = if (query.toUpperCase().contains(ALLOW_FILTERING)) query else s"$query $ALLOW_FILTERING"

    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(updatedQuery)
      rowsMap  = getRows(result)
    } yield rowsMap
  }

  override def insert(data: Map[String, Any]): Kleisli[IO, CqlSession, Option[Map[String, Any]]] = {
    val insertQuery = getInsertQuery(data)
    println(s"insertQuery: ${insertQuery.toString()}")

    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(insertQuery.toString)
      rowsMap  = getRows(result)
      _        = println(s"insert rowsMap: $rowsMap")
    } yield rowsMap.headOption
  }

  override def update(data: Map[String, Any]): Kleisli[IO, CqlSession, Option[Map[String, Any]]] = {
    val updateQuery = getUpdateQuery(data)
    println(s"updateQuery: ${updateQuery.toString()}")

    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(updateQuery.toString())
      rowsMap  = getRows(result)
      _        = println(s"update rowsMap: $rowsMap")
    } yield rowsMap.headOption
  }

  override def delete(pk: TPK): Kleisli[IO, CqlSession, Unit] = {
    for {
      session  <- Kleisli.ask[IO, CqlSession]
      statement = session.prepare(deleteQuery)
      result    = session.execute(statement.bind(pk))
      rowsMap   = getRows(result)
    } yield {
      println(s"deleted rows: $rowsMap")
    }
  }
}