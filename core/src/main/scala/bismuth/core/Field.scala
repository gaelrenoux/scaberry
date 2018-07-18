package bismuth.core

import bismuth.core

import scala.reflect.ClassTag

trait Field[-Source, Type] {

  val name: String

  val typeClassTag: ClassTag[Type]

  val sourceClassTag: ClassTag[_ >: Source]

  def apply[Target <: Source](target: Target): FieldApplication[Target, Type]
}

object Field {

  class Impl[-Source, Type](val name: String,
                            val getter: Source => Type)
                           (implicit
                            val typeClassTag: ClassTag[Type],
                            _sourceClassTag: ClassTag[Source] //parameter type needs to be [Source], not [_ >: Source]
                           ) extends Field[Source, Type] {

    override def apply[Target <: Source](target: Target): FieldApplication[Target, Type] =
      new core.FieldApplication.FieldApplicationImpl[Target, Type](
        target, getter
      )

    val sourceClassTag: ClassTag[_ >: Source] = _sourceClassTag

  }

}
