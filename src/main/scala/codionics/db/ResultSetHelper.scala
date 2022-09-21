package codionics.db

import com.datastax.oss.driver.api.core.cql.ResultSet
import com.datastax.oss.driver.api.core.cql.Row

import scala.jdk.CollectionConverters._

trait ResultSetHelper {

  def getRow(rs: ResultSet): Map[String, Any] = {
    if (rs == null) Map.empty
    else {
      val columns = rs.getColumnDefinitions.asScala.map(_.getName)
      println(s"columns: $columns")
      toNameValueMap(rs, rs.one())
    }
  }

  def getRows(rs: ResultSet): List[Map[String, Any]] = {
    if (rs == null) List.empty
    else {
      val columns = rs.getColumnDefinitions.asScala.map(_.getName)
      rs.all().asScala.map(row => toNameValueMap(rs, row)).toList
    }
  }

  def toNameValueMap(rs: ResultSet, row: Row): Map[String, Object] = {
    if (rs == null) Map.empty
    else {
      val columns = rs.getColumnDefinitions.asScala.map(_.getName)
      columns.map(col => (col.toString(), row.getObject(col))).toMap
    }
  }
}
