package scaberry.core.filter

import scaberry.core.filter.Filter._

trait CanCombineAnd[F[_] <: Filter[_], G[_] <: Filter[_], H[_] <: Filter[_]] {
  def and[A](f: F[A], g: G[A]): H[A]
}


object CanCombineAnd {

  import Filter.Fields

  def reversed[F[_] <: Filter[_], G[_] <: Filter[_], H[_] <: Filter[_]](cc: CanCombineAnd[G, F, H]): CanCombineAnd[F,
    G, H] = new CanCombineAnd[F, G, H] {
    override def and[A](f: F[A], g: G[A]): H[A] = cc.and(g, f)
  }

  implicit def emptyAndAny[F[_] <: Filter[_]]: CanCombineAnd[Empty, F, F] =
    new CanCombineAnd[Empty, F, F] {
      override def and[A](e: Empty[A], f: F[A]): F[A] = f
    }

  implicit def anyAndEmpty[F[_] <: Filter[_]]: CanCombineAnd[F, Empty, F] =
    reversed(emptyAndAny)

  implicit val fieldAndfield: CanCombineAnd[Field, Field, Fields] =
    new CanCombineAnd[Field, Field, Fields] {
      override def and[A](f: Field[A], g: Field[A]): Fields[A] = Filter.fields(f, g)
    }

  implicit val fieldsAndfield: CanCombineAnd[Fields, Field, Fields] =
    new CanCombineAnd[Fields, Field, Fields] {
      override def and[A](f: Fields[A], g: Field[A]): Fields[A] = f.add(g)
    }

  implicit val fieldAndfields: CanCombineAnd[Field, Fields, Fields] = reversed(fieldsAndfield)

  implicit val fieldsAndfields: CanCombineAnd[Fields, Fields, Fields] =
    new CanCombineAnd[Fields, Fields, Fields] {
      override def and[A](f: Fields[A], g: Fields[A]): Fields[A] = f.addAll(g)
    }

  implicit val default: CanCombineAnd[Filter, Filter, CompositeAnd] = new CanCombineAnd[Filter, Filter, CompositeAnd] {
    override def and[A](f: Filter[A], g: Filter[A]): CompositeAnd[A] = (f, g) match {
      case (c: CompositeAnd[A], d: CompositeAnd[A]) => c.addAll(d)
      case (c: CompositeAnd[A], _) => c.add(g)
      case (_, d: CompositeAnd[A]) => d.add(f)
      case (_, _) => compositeAnd(f, g)
    }
  }

  /** Operations */
  implicit class Ops[A, F[_] <: Filter[_]](wrapped: F[A]) {
    def and[G[_] <: Filter[_], H[_] <: Filter[_]](g: G[A])(implicit cc: CanCombineAnd[F, G, H]): H[A] = {
      cc.and(wrapped, g)
    }
  }

}
