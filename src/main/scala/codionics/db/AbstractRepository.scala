package codionics.db

import cats.data.Kleisli
import cats.effect.IO
import com.datastax.oss.driver.api.core.CqlSession
import scala.jdk.FutureConverters._

abstract class AbstractRepository[T, TPK] extends Repository[T, TPK] {

  override def getAll: Kleisli[IO, CqlSession, Seq[Map[String, TypeVal]]] =
    for {
      session <- Kleisli.ask[IO, CqlSession]
      result   = session.execute(selectQuery)
      rowsMap  = getRows(result)
    } yield rowsMap

  override def getByPK(pk: TPK): Kleisli[IO, CqlSession, Option[Map[String, TypeVal]]] =
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
    } yield rowMap.getOrElse("count", TypeVal.DEFAULT_LONG).value.asInstanceOf[Long]

  override def getByQuery(query: String): Kleisli[IO, CqlSession, Seq[Map[String, TypeVal]]] = ???

  override def insert(data: Map[String, Any]): Kleisli[IO, CqlSession, Option[Map[String, TypeVal]]] = ???

  override def update(id: String, data: Map[String, Any]): Kleisli[IO, CqlSession, Option[Map[String, TypeVal]]] = ???

  override def delete(id: String): Kleisli[IO, CqlSession, Unit] = ???
}
