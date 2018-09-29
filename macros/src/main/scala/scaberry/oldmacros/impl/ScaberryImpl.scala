package scaberry.oldmacros.impl

import scala.meta.Term
import scala.reflect.macros.whitebox

class ScaberryImpl(val c: whitebox.Context) {

  import c.universe._

  def impl(annottees: c.Tree*): c.Expr[Any] = {

    val name: scala.Symbol = c.prefix.tree match {
      case q"new scaberry($n)" => c.eval[scala.Symbol](c.Expr(n))
    }
    println(s"Name found: $name")

    /*
    val fields = clazzTree.children.flatMap(_.children)
    val names = fields.collect {
      case q"$_ val $n = $expr"  => n
    }
    println(s"Fields found: $names") */
    println("annotees types: " + annottees.map(_.getClass))
    val (clazz, companion) = annottees match {
      case List(c: ClassDef) => (c, q"object ${TermName(c.name.toString)} {def meta = ???}")
      case List(c: ClassDef, m: ModuleDef) => (c, m)
      case _ => c.abort(c.enclosingPosition, "WTH")
    }

    c.Expr[Any](q"$clazz; $companion")

  }
}