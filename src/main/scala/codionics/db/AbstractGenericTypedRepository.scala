package codionics.db

import cats.data.Kleisli
import cats.effect.IO
import com.datastax.oss.driver.api.core.CqlSession
import codionics.utils.MapUtils._
import codionics.utils.CaseClassUtils._

abstract class AbstractGenericTypedRepository[T <: Product, TPK] extends GenericTypedRepository[T, TPK] {

  protected val ALLOW_FILTERING: String = "ALLOW FILTERING"

  override def getAll: Kleisli[IO, CqlSession, Seq[T]] =
    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(selectAllQuery)
      rowsMap  = getRows(result)
    } yield rowsMap.map(_.toSimpleCaseClass)

  override def getByPK(pk: TPK): Kleisli[IO, CqlSession, Option[T]] =
    for {
      session  <- Kleisli.ask[IO, CqlSession]
      statement = session.prepare(selectByPkColumnQuery)
      result    = session.execute(statement.bind(pk))
      rowsMap   = getRows(result)
    } yield rowsMap.headOption.map(_.toSimpleCaseClass)

  override def getCount: Kleisli[IO, CqlSession, Long] =
    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(selectCountQuery)
      rowMap   = getRow(result)
      _        = println(s"rowMap: $rowMap")
    } yield rowMap.getOrElse("count", 0L).asInstanceOf[Long]

  override def getByQuery(query: String): Kleisli[IO, CqlSession, Seq[T]] = {
    val updatedQuery = if (query.toUpperCase().contains(ALLOW_FILTERING)) query else s"$query $ALLOW_FILTERING"

    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(updatedQuery)
      rowsMap  = getRows(result)
    } yield rowsMap.map(_.toSimpleCaseClass)
  }

  override def insert(data: T): Kleisli[IO, CqlSession, Option[T]] = {
    val insertQuery = getInsertQuery(data.toMap)
    println(s"insertQuery: ${insertQuery.toString()}")

    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(insertQuery.toString)
      rowsMap  = getRows(result)
      _        = println(s"insert rowsMap: $rowsMap")
    } yield rowsMap.headOption.map(_.toSimpleCaseClass)
  }

  override def update(data: T): Kleisli[IO, CqlSession, Option[T]] = {
    val updateQuery = getUpdateQuery(data.toMap)
    println(s"updateQuery: ${updateQuery.toString()}")

    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(updateQuery.toString())
      rowsMap  = getRows(result)
      _        = println(s"update rowsMap: $rowsMap")
    } yield rowsMap.headOption.map(_.toSimpleCaseClass)
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
