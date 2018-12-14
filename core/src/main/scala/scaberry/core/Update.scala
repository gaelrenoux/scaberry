package scaberry.core

import scaberry.core.Field.{Copier, Getter}
import scaberry.core.Update._

/** An update on a target object. */
trait Update[Target] extends Function1[Target, Target] {

  def update(t: Target): Target

  def apply(t: Target): Target = update(t)

  /** Combines two updates into one, which will be applied in order. */
  def and(other: Update[Target]): Update[Target] = new ComposedU[Target](other :: this :: Nil)

  /** Combines two updates into one, which will be applied in order. */
  def `|@|`(other: Update[Target]): Update[Target] = and(other)

  def compose[B](f: B => Target, g: Target => B): OperationU[B] = operation(b => g(this(f(b))))

}


object Update {

  /** Trait for Updates created someplace else, like macros. Can be extended outside. */
  trait CustomU[T] extends Update[T]

  /** Trait for a filter representable as a Tree over some field hierarchy. */
  sealed trait TreeU[T] extends Update[T]


  /** Empty update, returning the object itself. */
  final class NoChange[T] extends Update[T] with TreeU[T] {
    override def update(t: T): T = t
  }

  def identity[T]: Update[T] = new NoChange[T]

  /** Update with a new value. */
  final class ValueU[T](val value: T) extends Update[T] with TreeU[T] {
    override def update(target: T): T = value
  }

  def value[T](value: T) = new ValueU(value)

  /** Update by applying the given operation. */
  final class OperationU[T](op: T => T) extends Update[T] {
    override def update(target: T): T = op(target)
  }

  def operation[T](op: T => T) = new OperationU(op)

  /** Field update. Updates an object by updating one of that object's fields.
    *
    * @tparam T Target type to update
    * @tparam F Type of the updated field
    */
  final class FieldU[T, F] private[core](val name: Symbol, getter: Getter[T, F], copier: Copier[T, F], val update: Update[F]) extends Update[T] {
    override def update(target: T): T = copier(target, update.update(getter(target)))
  }

  def field[T, F](field: CopyableField[T, F], update: Update[F]): FieldU[T, F] = new FieldU(field.name, field.getter, field.copier, update)

  /** Tree field filter. Specific field filter where the filter is itself a Tree. */
  final class TreeFieldU[T, F] private[core](val name: Symbol, getter: Getter[T, F], copier: Copier[T, F], val update: TreeU[F]) extends TreeU[T] {
    override def update(target: T): T = copier(target, update.update(getter(target)))

    /* Specific definition of {{and}} for TreeFieldU, to return a BranchingFieldsU. */
    //TODO handle case with the same symbol on on this and other
    def and(other: TreeFieldU[T, _]): BranchingFieldsU[T] = new BranchingFieldsU[T](Map(
      name -> this,
      other.name -> other
    ))

    def `|@|`(other: TreeFieldU[T, _]): BranchingFieldsU[T] = and(other)

  }

  def field[T, F](field: CopyableField[T, F], update: TreeU[F]): TreeFieldU[T, F] = new TreeFieldU(field.name, field.getter, field.copier, update)

  /** Composed update, obtained by combining updates. */
  final class ComposedU[T] private[core](list: List[Update[T]]) extends Update[T] {
    override def update(target: T): T = operations.foldLeft(target) { (acc, op) => op.update(acc) }

    lazy val operations: List[Update[T]] = list.reverse

    /* Specific definition of {{and}} for Composed update, to avoid too deep a hierarchy. */
    override def and(other: Update[T]): Update[T] = new ComposedU[T](other :: list)
  }

  /** Multiple Tree field filters on the same level, not more than one per symbol. */
  final class BranchingFieldsU[T] private[core](branches: Map[Symbol, TreeU[T]]) extends TreeU[T] {
    override def update(target: T): T = branches.values.foldLeft(target) { (acc, op) => op.update(acc) }

    /* Specific definition of {{and}} for BranchingFieldsU, to return a BranchingFieldsU. */
    //TODO handle case with the same symbol on this and other
    def and(other: TreeFieldU[T, _]): BranchingFieldsU[T] = new BranchingFieldsU[T](
      branches + (other.name -> other)
    )

    def `|@|`(other: TreeFieldU[T, _]): BranchingFieldsU[T] = and(other)
  }

}