package codionics.db

import cats.data.Kleisli
import cats.effect.IO
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.ResultSet
import scala.jdk.CollectionConverters._
import com.datastax.oss.driver.api.core.CqlIdentifier
import scala.collection.mutable.Buffer
import com.datastax.oss.driver.api.core.cql.AsyncCqlSession
import com.datastax.oss.driver.api.core.cql.AsyncResultSet
import com.datastax.oss.driver.api.core.cql.Row
import com.datastax.oss.driver.api.querybuilder.QueryBuilder._
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.querybuilder.select.Select

case class TypeVal(dataType: String, value: Any) {

  def toValueString: String = value.toString()
}

object TypeVal {
  val DEFAULT_LONG = TypeVal("Long", 0L)
}

trait Repository[T, TPK] {

  // def keyspace: String

  def tableName: String

  def pkColumnName: String

  def getAll: Kleisli[IO, CqlSession, Seq[Map[String, TypeVal]]]

  def getByPK(pk: TPK): Kleisli[IO, CqlSession, Option[Map[String, TypeVal]]]

  def getCount: Kleisli[IO, CqlSession, Long]

  def getByQuery(query: String): Kleisli[IO, CqlSession, Seq[Map[String, TypeVal]]]

  def insert(data: Map[String, Any]): Kleisli[IO, CqlSession, Option[Map[String, TypeVal]]]

  def update(id: String, data: Map[String, Any]): Kleisli[IO, CqlSession, Option[Map[String, TypeVal]]]

  def delete(id: String): Kleisli[IO, CqlSession, Unit]

  // def getFullyQualifiedName: String = s"$keyspace.$tableName"

  def selectAll: Select = selectFrom(tableName).all()

  def selectQuery: SimpleStatement = selectAll.build()

  def selectCountQuery = s"SELECT COUNT(*) FROM $tableName"

  def selectByPkColumnQuery: SimpleStatement = selectAll.whereColumn(pkColumnName).isEqualTo(bindMarker()).build()

  def getRow(rs: ResultSet): Map[String, TypeVal] = {
    val columns = rs.getColumnDefinitions.asScala.map(_.getName)
    println(s"columns: $columns")
    toNameTypeValMap(rs, rs.one())
  }

  def getRows(rs: ResultSet): List[Map[String, TypeVal]] = {
    val columns = rs.getColumnDefinitions.asScala.map(_.getName)
    rs.all().asScala.map(row => toNameTypeValMap(rs, row)).toList
  }

  def toNameValueMap(rs: ResultSet, row: Row): Map[String, Object] = {
    val columns = rs.getColumnDefinitions.asScala.map(_.getName)
    columns.map(col => (col.toString(), row.getObject(col))).toMap
  }

  def toNameTypeValMap(rs: ResultSet, row: Row): Map[String, TypeVal] = {
    val nameValueMap = toNameValueMap(rs, row)
    nameValueMap.map { case (k, v) => (k, TypeVal(v.getClass.getSimpleName, v)) }
  }
}
