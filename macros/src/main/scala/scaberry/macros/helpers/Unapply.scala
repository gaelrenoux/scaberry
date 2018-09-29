package scaberry.macros.helpers

import scala.collection.immutable.Seq
import scala.meta.{Ctor, Lit, Mod, Term, Tree}

object Unapply {

  object Symbol {
    def unapply(arg: Tree): Option[scala.Symbol] = arg match {
      case Term.Apply(_, Seq(Lit(name: String))) => Some(scala.Symbol(name))
      case Lit(s: scala.Symbol) => Some(s)
      case _ => None
    }
  }

  object Annotation {
    def unapply(arg: Tree): Option[(String, Seq[Term.Arg])] = arg match {
      case Mod.Annot(Term.Apply(Ctor.Name(clazz), s)) => Some((clazz, s))
      case _ => None
    }
  }

}
