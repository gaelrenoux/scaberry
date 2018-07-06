package bismuth.core

import scala.reflect.api._


case class Field[Source, Type](
                                name: String,
                                sourceType: TypeTag[Source],
                                fieldType: TypeTag[Type]
                              ) {

  def apply[Target <: Source](target: Target): Applyer[Target, Type] = ???

}

