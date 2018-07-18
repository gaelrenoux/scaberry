package bismuth.core

trait FieldApplication[Target, Type] {

  def get: Type
}

object FieldApplication {

  trait Copyable[Target, Type] extends FieldApplication[Target, Type] {
    def copy(a: Type): Target
  }

  class FieldApplicationImpl[Target, Type](
                                            target: Target,
                                            getter: Target => Type
                                          ) extends FieldApplication[Target, Type] {
    override def get: Type = getter(target)
  }

}