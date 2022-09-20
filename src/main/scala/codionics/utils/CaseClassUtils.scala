package codionics.utils

object CaseClassUtils {

  implicit class TCaseClassOps[TCaseClass <: Product](private val tCaseClass: TCaseClass) extends AnyVal {

    def toMap: Map[String, Any] = {
      val fields = tCaseClass.getClass.getDeclaredFields.map(_.getName)
      val values = tCaseClass.productIterator.toSeq
      val tuples = fields.zip(values)

      val nested = tuples.map { case (k, v) =>
        v match {
          case product: Product =>
            val inner = product.toMap
            (k, inner)
          case _                =>
            (k, v)
        }
      }
      nested.toMap
    }
  }
}
