package scaberry.core.filter

import scaberry.core.filter.Filter._

trait CanCombineAnd[F[_] <: Filter[_], G[_] <: Filter[_], H[_] <: Filter[_]] {
  def and[A](f: F[A], g: G[A]): H[A]
}


object CanCombineAnd {

  implicit def reversed[F[_] <: Filter[_], G[_] <: Filter[_], H[_] <: Filter[_]]
    (implicit cc: CanCombineAnd[G, F, H]): CanCombineAnd[F, G, H] = new CanCombineAnd[F, G, H] {
    override def and[A](f: F[A], g: G[A]): H[A] = cc.and(g, f)
  }

  implicit def emptyAndAny[F[_] <: Filter[_]]: CanCombineAnd[Empty, F, F] = new CanCombineAnd[Empty, F, F] {
    override def and[A](e: Empty[A], f: F[A]): F[A] = f
  }

  /** Operations on */
  implicit class Ops[A, F[_] <: Filter[_]](wrapped: F[A]) {
    def and[G[_] <: Filter[_], H[_] <: Filter[_]](g: G[A])(implicit cc: CanCombineAnd[F, G, H]): H[A] = {
      cc.and(wrapped, g)
    }
  }

}
