package scalberto.macros

import scala.collection.immutable.Seq
import scala.meta._

object Fields {

  def namesAndDeclarations(clazz: Defn.Class, isCopyable: Boolean): (Seq[Term.Name], Seq[Defn.Val]) = {
    val srcTpe = clazz.name
    val fieldTerms = clazz.ctor.paramss.flatten

    fieldTerms.map { field =>
      val fNameValueString = field.name.value
      val fName = Term.Name(fNameValueString)
      val fType = field.decltpe.get.asInstanceOf[Type]
      val fNameSym = Lit.Symbol(scala.Symbol(fName.value))

      val scaffield = if (isCopyable) {
        val copier = q"(src: $srcTpe, a: $fType) => src.copy($fName = a)"
        q"""
          val ${Pat.Var.Term(fName)}: scalberto.core.CopyableField[$srcTpe, $fType] =
            new scalberto.core.CopyableField[$srcTpe, $fType]($fNameSym, _.$fName, $copier)
          """

      } else {
        q"""
          val ${Pat.Var.Term(fName)}: scalberto.core.Field[$srcTpe, $fType] =
            new scalberto.core.Field[$srcTpe, $fType]($fNameSym, _.$fName)"""
      }

      (fName, scaffield)
    }.unzip
  }

}
