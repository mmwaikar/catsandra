package codionics.utils

import scala.reflect.ClassTag

object MapUtils {

  implicit class MapTOps(private val m: Map[String, Any]) extends AnyVal {

    /** Converts a simple (non-nested) map to a case class with matching fields.
      *
      * @param classTag
      *   an implicit parameter of type ClassTag[case class name]
      * @tparam T
      *   the name of the case class
      * @return
      *   an instance of the case class
      */
    def toSimpleCaseClass[T]()(implicit classTag: ClassTag[T]): T = {
      val ctor = classTag.runtimeClass.getConstructors.head
      val args = classTag.runtimeClass.getDeclaredFields.map(f => m(f.getName).asInstanceOf[AnyRef])
      ctor.newInstance(args: _*).asInstanceOf[T]
    }
  }
}
