package scaberry.core.filter

import scaberry.core.{Getter, Field => CField}

/**
  *
  * @tparam A Target type to filter
  */
sealed trait Filter[-A] extends Function1[A, Boolean] {

  def verify(target: A): Boolean

  def apply(target: A): Boolean = verify(target)

  def &&[B <: A](that: Filter[B]): Filter[B] =
    Filter.compositeAnd(this, that)

  def ||[B <: A](that: Filter[B]): Filter[B] =
    Filter.compositeOr(this, that)

}

object Filter {

  /** Empty filter, accepts any value. */
  sealed class Empty[-A] extends Filter[A] {
    override def verify(a: A): Boolean = true

    override def &&[B <: Any](that: Filter[B]): Filter[B] = that

    override def ||[B](that: Filter[B]): Filter[B] = Empty
  }

  object Empty extends Empty[Any]

  /** Commodity function to define an empty filter */
  def empty[A]: Empty[A] = Empty

  /** Value filter, accepts only value equals to its argument. */
  final class Value[-A] private[core](value: A) extends Filter[A] {
    override def verify(a: A): Boolean = a == value
  }

  /** Commodity function to define a Value */
  def value[A](value: A): Value[A] = new Value(value)

  /** Filter on a collection by checking if the filter matches any element */
  final class CollectionAny[-A] private[core](filter: Filter[A]) extends Filter[Traversable[A]] {
    override def verify(a: Traversable[A]): Boolean = a.exists(filter)
  }

  /** Commodity function to define a CollectionAny */
  def collectionAny[A](filter: Filter[A]): Value[A] = new Value(value)

  /** Filter on a collection by checking if the filter matches all elements */
  final class CollectionAll[-A] private[core](filter: Filter[A]) extends Filter[Traversable[A]] {
    override def verify(a: Traversable[A]): Boolean = a.forall(filter)
  }

  /** Commodity function to define a CollectionAll */
  def collectionAll[A](filter: Filter[A]): Value[A] = new Value(value)

  /** Filter on an object by filtering on one of that object's fields.
    *
    * @tparam A Target type to filter
    */
  sealed trait Field[-A] extends Filter[A] {
    val name: Symbol
    val filter: Filter[_]
  }

  /**
    * Inner class to ensure the type of the wrapped filter matches the type of the getter.
    * @tparam A Target type to filter
    * @tparam B Type of the filtered field
    */
  private final class OvertypedField[-A, -B] private[core](
      val name: Symbol,
      getter: Getter[A, B],
      val filter: Filter[B]
  ) extends Field[A] {
    override def verify(a: A): Boolean = filter(getter(a))
  }

  /** Commodity function to define a Field */
  def field[A, B](field: CField[A, B], filter: Filter[B]): Field[A] = new OvertypedField(field.name, field.getter, filter)

  /** Filter on multiple fields. */
  final class Fields[-A] private[core](private val seq: Seq[Field[A]]) extends Filter[A] {
    override def verify(a: A): Boolean = seq.forall(_.verify(a))

    private[filter] def add[B <: A](f: Field[B]) = new Fields[B](seq :+ f)

    private[filter] def addAll[B <: A](fs: Fields[B]) = new Fields[B](seq ++ fs.seq)
  }

  /** Commodity function to define a Fields */
  def fields[A](fields: Field[A]*): Fields[A] = new Fields(fields)

  /** Filters put together, all must match. */
  final class CompositeAnd[-A] private[core](private val seq: Seq[Filter[A]]) extends Filter[A] {

    override def verify(a: A): Boolean = seq.forall(_.verify(a))

    private[filter] def add[B <: A](f: Filter[B]) = new CompositeAnd[B](seq :+ f)

    private[filter] def addAll[B <: A](fs: CompositeAnd[B]) = new CompositeAnd[B](seq ++ fs.seq)
  }

  /** Commodity function to define a CompositeAnd */
  def compositeAnd[A](filters: Filter[A]*): CompositeAnd[A] = new CompositeAnd(filters)

  /** Filters put together, any must match. */
  final class CompositeOr[-A] private[core](private val seq: Seq[Filter[A]]) extends Filter[A] {

    override def verify(a: A): Boolean = seq.exists(_.verify(a))

    private[filter] def add[B <: A](f: Filter[B]) = new CompositeOr[B](seq :+ f)

    private[filter] def addAll[B <: A](fs: CompositeOr[B]) = new CompositeOr[B](seq ++ fs.seq)
  }

  /** Commodity function to define a CompositeOr */
  def compositeOr[A](filters: Filter[A]*): CompositeOr[A] = new CompositeOr(filters)

}