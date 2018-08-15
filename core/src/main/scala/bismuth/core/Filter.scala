package bismuth.core

trait Filter[-Target] extends Function1[Target, Boolean] {

  def verify(t: Target): Boolean

  def apply(t: Target): Boolean = verify(t)

}


object Filter {

  object None extends Filter[Any] {
    override def verify(t: Any): Boolean = true
  }

  implicit class Value[-T](value: T) extends Filter[T] {
    override def verify(target: T): Boolean = target == value
  }

  implicit class Operation[-T](op: T => Boolean) extends Filter[T] {
    override def verify(target: T): Boolean = op(target)
  }

  /** Trait for Filters created for case objects through the macro. */
  trait Composite[T] extends Filter[T]

}