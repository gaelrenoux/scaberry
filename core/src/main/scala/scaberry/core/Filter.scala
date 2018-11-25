package scaberry.core

import scaberry.core.Field.Getter
import scaberry.core.Filter._

/** A filter on a target object. */
sealed trait Filter[-Target] extends Function1[Target, Boolean] {

  def verify(target: Target): Boolean

  def apply(target: Target): Boolean = verify(target)

  /** Combines two filter into one. Target must match both combined filters. */
  def and[T2 <: Target](other: Filter[T2]): Filter[T2] = new ComposedF[T2](other :: this :: Nil)

  /** Combines two filter into one. Target must match both combined filters. */
  def `|@|`[T2 <: Target](other: Filter[T2]): Filter[T2] = and(other)

}


object Filter {

  /** Empty filter, accepts any value. */
  object None extends Filter[Any] {
    override def verify(t: Any): Boolean = true
  }

  /** Value filter, accepts only value equals to its argument. */
  final class ValueF[-T](value: T) extends Filter[T] {
    override def verify(target: T): Boolean = target == value
  }

  def value[T](value: T) = new ValueF(value)

  /** Operation filter, accepts values for which the argument returns true. */
  final class OperationF[-T](op: T => Boolean) extends Filter[T] {
    override def verify(target: T): Boolean = op(target)
  }

  def operation[T](op: T => Boolean) = new OperationF(op)

  /** Field filter. Filter on an object by filtering on one of that object's fields. */
  final class FieldF[-T, -A] private[core](val name: Symbol, getter: Getter[T, A], val fieldFilter: Filter[A]) extends Filter[T] {
    override def verify(target: T): Boolean = fieldFilter(getter(target))
  }

  def field[T, F](field: Field[T, F], filter: Filter[F]): FieldF[T, F] = new FieldF(field.name, field.getter, filter)


  /** Composed filter, obtained by combining filters. */
  final class ComposedF[-T] private[core](list: List[Filter[T]]) extends Filter[T] {
    override def verify(target: T): Boolean = list.forall(_.verify(target))

    lazy val subfilters: List[Filter[T]] = list.reverse

    /* Specific definition of {{and}} for Composed filter, to avoid too deep a hierarchy. */
    override def and[T2 <: T](other: Filter[T2]): Filter[T2] = new ComposedF[T2](other :: list)
  }

  /** Trait for Filters created through macros. */
  trait CustomF[-T] extends Filter[T]

}