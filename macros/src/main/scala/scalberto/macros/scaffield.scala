package scalberto.macros

import scala.annotation._
import scala.meta._

/** Put the annotation on the case class to generate the meta-object on the companion. */
@compileTimeOnly("Should be used only in compile time.")
class scaffield extends StaticAnnotation {

  inline def apply(defn: Any): Any = meta {

    def isCaseClass(clazz: Defn.Class): Boolean = clazz.mods.exists(_.isInstanceOf[Mod.Case])

    val (clz, companion) = defn match {
      case Term.Block(List(cls: Defn.Class, companion: Defn.Object)) if isCaseClass(cls) => (cls, companion)
      case cls: Defn.Class if isCaseClass(cls) => (cls, q"object ${Term.Name(cls.name.value)}")
      //case _ => abort("@Metadata must annotate an case class")
    }
    val srcTpe = clz.name
    val fieldTerms = clz.ctor.paramss.flatten

    val (scafieldNames, scafieldDecls) = fieldTerms.map { field =>
      val fNameValueString = field.name.value
      val fName = Term.Name(fNameValueString)
      val fType = field.decltpe.get.asInstanceOf[Type]
      val fNameSym = Lit.Symbol(scala.Symbol(fName.value))

      val copier = q"{ (src: $srcTpe, a: $fType) => src.copy($fName = a) }"
      val scafield = q"val ${Pat.Var.Term(fName)} = new scalberto.core.CopyableField[$srcTpe, $fType]($fNameSym, _.$fName, $copier)"

      (fName, scafield)
    }.unzip

    val qualifiedScafieldNames = scafieldNames.map(n => q"fields.$n")

    val metaObject =
      q"""
         object meta {
           object fields {
             ..$scafieldDecls
           }

           val orderedFields = Seq(..$qualifiedScafieldNames)

           val fieldsMap = orderedFields.groupBy(_.name)
         }
       """

    val newCompanion = companion.copy(
      templ = companion.templ.copy(
        stats = Some(companion.templ.stats.getOrElse(Nil) :+ metaObject)
      )
    )

    q"$clz; $newCompanion"

  }
}
