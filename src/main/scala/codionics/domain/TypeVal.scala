package codionics.domain

case class TypeVal(dataType: String, value: Any) {

  def toValueString: String = value.toString()
}

object TypeVal {
  val DEFAULT_LONG = TypeVal("Long", 0L)
}
