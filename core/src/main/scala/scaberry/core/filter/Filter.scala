package scaberry.core.filter

import scaberry.core.{Getter, Field => CField}

/**
  *
  * @tparam A Target type to filter
  * @tparam S Serialization level
  */
sealed trait Filter[-A, +S <: Ser] {

  def verify(target: A): Boolean

  def apply(target: A): Boolean = verify(target)

  def &&[B <: A, T <: Ser, U >: S with T with Ser.Complex <: Ser](that: Filter[B, T]): Filter[B, U] =
    Filter.compositeAnd[B, U, U](this, that)

  def ||[B <: A, T >: S <: Ser, U >: T with Ser.Complex <: Ser](that: Filter[B, T]): Filter[B, U] =
    Filter.compositeOr[B, T, U](this, that)

}

object Filter {

  /** Empty filter, accepts any value. */
  object Empty extends Filter[Any, Ser.Simple] {
    override def verify(a: Any): Boolean = true

    override def &&[B <: Any, T >: Ser.Simple <: Ser, U >: T with Ser.Complex <: Ser](that: Filter[B, T]): Filter[B, U] = that

    override def ||[B, T <: Ser, U >: Ser.Simple <: Ser](that: Filter[B, T]): Filter[B, Ser.Simple] = this
  }

  /** Value filter, accepts only value equals to its argument. */
  final class Value[-A] private[core](value: A) extends Filter[A, Ser.Simple] {
    override def verify(a: A): Boolean = a == value
  }

  /** Commodity function to define a Value */
  def value[A](value: A): Filter[A, Ser.Simple] = new Value(value)

  /** Filter on an object by filtering on one of that object's fields.
    *
    * @tparam A Target type to filter
    * @tparam B Type of the filtered field
    */
  final class Field[-A, -B, S <: Ser] private[core](
      val name: Symbol,
      getter: Getter[A, B],
      val filter: Filter[B, S]
  ) extends Filter[A, S] {
    override def verify(a: A): Boolean = filter(getter(a))
  }

  /** Commodity function to define a Field */
  def field[A, B, S <: Ser](field: CField[A, B], filter: Filter[B, S]): Field[A, B, S] = new Field(field
    .name, field.getter, filter)

  /** Filter on multiple fields. */
  final class Fields[-A, S <: Ser] private[core](seq: Seq[Field[A, _, S]]) extends Filter[A, S] {
    override def verify(a: A): Boolean = seq.forall(_.verify(a))
  }

  /** Commodity function to define a Fields */
  def fields[A, S <: Ser](fields: Field[A, _, S]*): Fields[A, S] = new Fields(fields)

  /** Filters put together, all must match. */
  final class CompositeAnd[-A, SA <: Ser, S >: SA with Ser.Complex <: Ser] private[core](
      seq: Seq[Filter[A, SA]]
  ) extends Filter[A, S] {

    override def verify(a: A): Boolean = seq.forall(_.verify(a))
/*
    override def &&[B <: A, SB <: Ser](that: Filter[B, SB]): Filter[B, Ser] =
      Filter.compositeAnd[B, Ser, Ser](this.seq :+ that: _*)*/
  }

  /** Commodity function to define a CompositeAnd */
  def compositeAnd[A, SA <: Ser, S >: SA with Ser.Complex <: Ser]
    (filters: Filter[A, SA]*): CompositeAnd[A, SA, S] =
    new CompositeAnd(filters)

  /** Filters put together, any must match. */
  final class CompositeOr[-A, SA <: Ser, S >: SA with Ser.Complex <: Ser] private[core](
      seq: Seq[Filter[A, SA]]
  ) extends Filter[A, S] {

    override def verify(a: A): Boolean = seq.exists(_.verify(a))
  }

  /** Commodity function to define a CompositeOr */
  def compositeOr[A, SA <: Ser, S >: SA with Ser.Complex <: Ser]
    (filters: Filter[A, SA]*): CompositeOr[A, SA, S] =
    new CompositeOr(filters)

}