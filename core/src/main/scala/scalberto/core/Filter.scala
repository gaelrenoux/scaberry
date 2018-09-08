package scalberto.core

import scalberto.core.Field.Getter
import scalberto.core.Filter._

/** A filter on a target object. */
sealed trait Filter[-Target] extends Function1[Target, Boolean] {

  def verify(target: Target): Boolean

  def apply(target: Target): Boolean = verify(target)

  /** Combines two filter into one. Target must match both combined filters. */
  def and[T2 <: Target](other: Filter[T2]): Filter[T2] = new Composed[T2](other :: this :: Nil)

  /** Combines two filter into one. Target must match both combined filters. */
  def `|@|`[T2 <: Target](other: Filter[T2]): Filter[T2] = and(other)

}


object Filter {

  /** Empty filter, accepts any value. */
  object None extends Filter[Any] {
    override def verify(t: Any): Boolean = true
  }

  /** Value filter, accepts only value equals to its argument. */
  final class Value[-T](value: T) extends Filter[T] {
    override def verify(target: T): Boolean = target == value
  }

  def value[T](value: T) = new Value(value)

  /** Operation filter, accepts values for which the argument returns true. */
  final class Operation[-T](op: T => Boolean) extends Filter[T] {
    override def verify(target: T): Boolean = op(target)
  }

  def operation[T](op: T => Boolean) = new Operation(op)

  /** Field filter. Filter on an object by filtering on one of that object's fields. */
  final class Field[-T, -A] private[core](val name: Symbol, getter: Getter[T, A], val fieldFilter: Filter[A]) extends Filter[T] {
    override def verify(target: T): Boolean = fieldFilter(getter(target))
  }

  /** Composed filter, obtained by combining filters. */
  final class Composed[-T] private[core](list: List[Filter[T]]) extends Filter[T] {
    override def verify(target: T): Boolean = list.forall(_.verify(target))

    lazy val subfilters: List[Filter[T]] = list.reverse

    /* Specific definition of {{and}} for Composed filter, to avoid too deep a hierarchy. */
    override def and[T2 <: T](other: Filter[T2]): Filter[T2] = new Composed[T2](other :: list)
  }

  /** Trait for Filters created through macros. */
  trait Custom[-T] extends Filter[T]

}