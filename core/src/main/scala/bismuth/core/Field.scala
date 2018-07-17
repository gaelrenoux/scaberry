package bismuth.core

import scala.reflect.ClassTag
import scala.reflect.runtime.{universe => ru}

trait Field[-Source, Type] {

  val name: String

  //TODO have compile-time stuff instead of runtime, or move it
  def apply[Target <: Source : ClassTag : ru.TypeTag](target: Target): FieldApplication[Target, Type]

}
