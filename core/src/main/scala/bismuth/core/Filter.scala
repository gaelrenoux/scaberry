package bismuth.core

import bismuth.core.Filter.Composed

sealed trait Filter[-Target] extends Function1[Target, Boolean] {

  def verify(target: Target): Boolean

  def apply(target: Target): Boolean = verify(target)

  def and[T2 <: Target](other: Filter[T2]): Filter[T2] = new Composed[T2](other :: this :: Nil)

  def `|@|`[T2 <: Target](other: Filter[T2]): Filter[T2] = and(other)

}


object Filter {

  object None extends Filter[Any] {
    override def verify(t: Any): Boolean = true
  }

  final class Value[-T](value: T) extends Filter[T] {
    override def verify(target: T): Boolean = target == value
  }

  def value[T](value: T) = new Value(value)

  final class Operation[-T](op: T => Boolean) extends Filter[T] {
    override def verify(target: T): Boolean = op(target)
  }

  def operation[T](op: T => Boolean) = new Operation(op)

  final class Field[T, A] private[core] (val name: Symbol, getter: T => A, val filter: Filter[A]) extends Filter[T] {
    override def verify(target: T): Boolean = filter(getter(target))
  }

  final class Composed[T] private[core] (list: List[Filter[T]]) extends Filter[T] {
    override def verify(target: T): Boolean = list.forall(_.verify(target))

    lazy val subfilters: List[Filter[T]] = list.reverse

    override def and[T2 <: T](other: Filter[T2]): Filter[T2] = new Composed[T2](other :: this :: Nil)
  }

  /** Trait for Filters created for case objects through the macro. */
  trait Custom[T] extends Filter[T]

}