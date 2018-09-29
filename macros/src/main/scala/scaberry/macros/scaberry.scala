package scaberry.macros

import scala.annotation._
import scala.meta._

/** Put the annotation on the case class to generate the meta-object on the companion. */
@compileTimeOnly("Should be used only in compile time.")
class scaberry extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    val (clazz, companion) = defn match {
      case Term.Block(List(cls: Defn.Class, companion: Defn.Object)) => (cls, companion)
      case cls: Defn.Class => (cls, q"object ${Term.Name(cls.name.value)}")
      case _ => abort(defn.pos, "@scaberry must annotate a class or case class")
    }

    val isCopyable = clazz.mods.exists(_.isInstanceOf[Mod.Case])
    val srcTpe = clazz.name
    val (sbNames, sbDeclarations) = SbFields.namesAndDeclarations(clazz, isCopyable)

    val qualifiedSbNames = sbNames.map(n => q"fields.$n")
    val metaObjectName = Term.Name("meta")
    val metaObject =
      if (isCopyable)
        q"""
          object $metaObjectName extends scaberry.macros.CopyableMeta[$srcTpe] {
            object fields {
              ..$sbDeclarations
            }

            val orderedFields: Seq[scaberry.core.CopyableField[$srcTpe, _]] =
              Seq(..$qualifiedSbNames)

            val fieldsMap: Map[scala.Symbol, scaberry.core.CopyableField[$srcTpe, _]] =
              orderedFields.groupBy(_.name).map { case (k, v) => (k, v.head) }.toMap
          }
        """
      else
        q"""
          object meta extends scaberry.macros.Meta[$srcTpe] {
            object fields {
              ..$sbDeclarations
            }

            val orderedFields: Seq[scaberry.core.Field[$srcTpe, _]] =
              Seq(..$qualifiedSbNames)

            val fieldsMap: Map[scala.Symbol, scaberry.core.Field[$srcTpe, _]] =
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
