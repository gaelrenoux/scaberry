package scaberry.core.filter


sealed trait FilterAst[-A]

object FilterAst {

  trait Simple[-A] extends FilterAst[A]

  case object Empty extends Simple[Any]

  case class SimpleValue[A](value: A) extends Simple[A]

  case class SimpleFields[A](subs: Map[Symbol, Simple[_]]) extends Simple[A]

}
