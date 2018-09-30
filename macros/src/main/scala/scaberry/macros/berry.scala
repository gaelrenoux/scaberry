package scaberry.macros

import scaberry.macros.helpers.Log

import scala.annotation._
import scala.meta._

/** Put the annotation on the case class to generate the meta-object on the companion. */
@compileTimeOnly("Should be used only in compile time.")
class berry(val name: scala.Symbol = 'meta) extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    val (clazz, companion) = defn match {
      case Term.Block(List(cls: Defn.Class, companion: Defn.Object)) => (cls, companion)
      case cls: Defn.Class => (cls, q"object ${Term.Name(cls.name.value)}")
      case x => abort(defn.pos, s"@berry must annotate a class or case class. Not:\n$x")
    }

    val berryName = this match {
      case q"new $_()" => "meta"
      case q"new $_(${Lit(n: scala.Symbol)})" => n.toString
      case q"new $_(scala.Symbol(${Lit(n: String)}))" => n
      case _ => abort("@berry parameters should be literals")
    }

    val metaObject = MetaFactory.meta(clazz, berryName)

    val newCompanion = companion.copy(
      templ = companion.templ.copy(
        stats = Some(companion.templ.stats.getOrElse(Nil) :+ metaObject)
      )
    )
    Log.debug(newCompanion.toString)

    q"$clazz; $newCompanion"
  }
}

object berry {
  /** Put the annotation on the case class to generate the meta-object on the companion, using the public vals instead of
    * the constructor arguments. */
  @compileTimeOnly("Should be used only in compile time.")
  class fromPublicVals(val name: scala.Symbol = 'meta) extends StaticAnnotation {

    inline def apply(defn: Any): Any = meta {

      val (clazz, companion) = defn match {
        case Term.Block(List(cls: Defn.Class, companion: Defn.Object)) => (cls, companion)
        case cls: Defn.Class => (cls, q"object ${Term.Name(cls.name.value)}")
        case x => abort(defn.pos, s"@berry.fromVals must annotate a class or case class. Not:\n$x")
      }

      val berryName = this match {
        case q"new $_()" => "meta"
        case q"new $_(${Lit(n: scala.Symbol)})" => n.toString
        case q"new $_(scala.Symbol(${Lit(n: String)}))" => n
        case _ => abort("@berry parameters should be literals")
      }

      val metaObject = MetaFactory.meta(clazz, berryName, fromVals = true)

      val newCompanion = companion.copy(
        templ = companion.templ.copy(
          stats = Some(companion.templ.stats.getOrElse(Nil) :+ metaObject)
        )
      )
      Log.debug(newCompanion.toString)

      q"$clazz; $newCompanion"
    }
  }

}