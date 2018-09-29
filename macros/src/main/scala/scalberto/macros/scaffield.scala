package scalberto.macros

import scala.annotation._
import scala.meta._

/** Put the annotation on the case class to generate the meta-object on the companion. */
@compileTimeOnly("Should be used only in compile time.")
class scaffield extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    val (clazz, companion) = defn match {
      case Term.Block(List(cls: Defn.Class, companion: Defn.Object)) => (cls, companion)
      case cls: Defn.Class => (cls, q"object ${Term.Name(cls.name.value)}")
      case _ => abort(defn.pos, "@scaffield must annotate a class or case class")
    }

    val isCopyable = clazz.mods.exists(_.isInstanceOf[Mod.Case])
    val srcTpe = clazz.name
    val (scaffieldNames, scaffieldDeclarations) = Fields.namesAndDeclarations(clazz, isCopyable)

    val qualifiedScaffieldNames = scaffieldNames.map(n => q"fields.$n")
    val metaObjectName = Term.Name("meta")
    val metaObject =
      if (isCopyable)
        q"""
          object $metaObjectName extends scalberto.macros.CopyableMeta[$srcTpe] {
            object fields {
              ..$scaffieldDeclarations
            }

            val orderedFields: Seq[scalberto.core.CopyableField[$srcTpe, _]] =
              Seq(..$qualifiedScaffieldNames)

            val fieldsMap: Map[scala.Symbol, scalberto.core.CopyableField[$srcTpe, _]] =
              orderedFields.groupBy(_.name).map { case (k, v) => (k, v.head) }.toMap
          }
        """
      else
        q"""
          object meta extends scalberto.macros.Meta[$srcTpe] {
            object fields {
              ..$scaffieldDeclarations
            }

            val orderedFields: Seq[scalberto.core.Field[$srcTpe, _]] =
              Seq(..$qualifiedScaffieldNames)

            val fieldsMap: Map[scala.Symbol, scalberto.core.Field[$srcTpe, _]] =
              orderedFields.groupBy(_.name).map { case (k, v) => (k, v.head) }.toMap
          }
        """

    val newCompanion = companion.copy(
      templ = companion.templ.copy(
        stats = Some(companion.templ.stats.getOrElse(Nil) :+ metaObject)
      )
    )

    q"$clazz; $newCompanion"
  }
}
