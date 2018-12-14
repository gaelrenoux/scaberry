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

  override def compose[B](f: B => Target): Filter[B] = operation(b => this (f(b)))

}


object Filter {

  /** Trait for Filters created someplace else, like macros. Can be extended outside. */
  trait CustomF[-T] extends Filter[T]

  /** Trait for a filter representable as a Tree over some field hierarchy. */
  sealed trait TreeF[-T] extends Filter[T]


  /** Empty filter, accepts any value. */
  object None extends Filter[Any] with TreeF[Any] {
    override def verify(t: Any): Boolean = true
  }

  val none: Filter[Any] = None

  /** Value filter, accepts only value equals to its argument. */
  final class ValueF[-T] private[core](value: T) extends Filter[T] with TreeF[T] {
    override def verify(target: T): Boolean = target == value
  }

  def value[T](value: T) = new ValueF(value)

  /** Operation filter, accepts values for which the argument returns true. */
  final class OperationF[-T] private[core](op: T => Boolean) extends Filter[T] {
    override def verify(target: T): Boolean = op(target)
  }

  def operation[T](op: T => Boolean) = new OperationF(op)

  /** Field filter. Filter on an object by filtering on one of that object's fields.
    *
    * @tparam T Target type to filter
    * @tparam F Type of the filtered field
    */
  sealed class FieldF[-T, -F] private[core](val name: Symbol, getter: Getter[T, F], val filter: Filter[F]) extends Filter[T] {
    override def verify(target: T): Boolean = filter(getter(target))
  }

  def field[T, F](field: Field[T, F], filter: Filter[F]): FieldF[T, F] = new FieldF(field.name, field.getter, filter)

  /** Tree field filter. Specific field filter where the filter is itself a Tree. */
  final class TreeFieldF[-T, -F] private[core](val name: Symbol, getter: Getter[T, F], val filter: TreeF[F]) extends TreeF[T] {
    override def verify(target: T): Boolean = filter(getter(target))

    /* Specific definition of {{and}} for TreeFieldF, to return a BranchingFieldsF. */
    //TODO handle case with the same symbol on on this and other
    def and[T2 <: T](other: TreeFieldF[T2, _]): BranchingFieldsF[T2] = new BranchingFieldsF[T2](Map(
      name -> this,
      other.name -> other
    ))

    def `|@|`[T2 <: T](other: TreeFieldF[T2, _]): BranchingFieldsF[T2] = and(other)

  }

  def field[T, F](field: Field[T, F], filter: TreeF[F]): TreeFieldF[T, F] = new TreeFieldF(field.name, field.getter, filter)


  /** Composed filter, obtained by combining arbitrary filters. */
  final class ComposedF[-T] private[core](list: List[Filter[T]]) extends Filter[T] {
    override def verify(target: T): Boolean = subfilters.forall(_.verify(target))

    lazy val subfilters: List[Filter[T]] = list.reverse

    /** Specific definition of {{and}} for Composed filter, to avoid too deep a hierarchy. */
    override def and[T2 <: T](other: Filter[T2]): Filter[T2] = new ComposedF[T2](other :: list)
  }

  /** Multiple Tree field filters on the same level, not more than one per symbol. */
  final class BranchingFieldsF[T] private[core](branches: Map[Symbol, TreeF[T]]) extends TreeF[T] {
    override def verify(target: T): Boolean = branches.values.forall(_.verify(target))

    /* Specific definition of {{and}} for BranchingFieldsF, to return a BranchingFieldsF. */
    //TODO handle case with the same symbol on this and other
    def and[T2 <: T](other: TreeFieldF[T2, _]): BranchingFieldsF[T2] = new BranchingFieldsF[T2](
      branches + (other.name -> other)
    )

    def `|@|`[T2 <: T](other: TreeFieldF[T2, _]): BranchingFieldsF[T2] = and(other)
  }

}