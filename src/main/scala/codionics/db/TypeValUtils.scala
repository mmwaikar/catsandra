package codionics.db

import codionics.domain._

object TypeValUtils {

  implicit class TypeValOps(val m: Map[String, TypeVal]) extends AnyVal {
    def toNameValueMap: Map[String, Any] = m.map { case (k, v) => (k, v.value) }
  }

  implicit class TypeValSeqOps(val rows: Seq[Map[String, TypeVal]]) extends AnyVal {
    def toSeqNameValueMap: Seq[Map[String, Any]] = rows.map(_.toNameValueMap)
  }
}
