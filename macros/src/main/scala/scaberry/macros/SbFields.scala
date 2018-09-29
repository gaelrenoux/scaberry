package scaberry.macros

import scala.collection.immutable.Seq
import scala.meta._

object SbFields {

  def namesAndDeclarations(clazz: Defn.Class, isCopyable: Boolean): (Seq[Term.Name], Seq[Defn.Val]) = {
    val srcTpe = clazz.name
    val fieldTerms = clazz.ctor.paramss.flatten

    fieldTerms.map { fieldTerm =>
      val fieldName = Term.Name(fieldTerm.name.value)
      val fieldType = fieldTerm.decltpe.get.asInstanceOf[Type]
      val fieldNameSym = Lit.Symbol(scala.Symbol(fieldName.value))

      val sbField = if (isCopyable) {
        val copier = q"(src: $srcTpe, a: $fieldType) => src.copy($fieldName = a)"
        q"""
          val ${Pat.Var.Term(fieldName)}: scaberry.core.CopyableField[$srcTpe, $fieldType] =
            new scaberry.core.CopyableField[$srcTpe, $fieldType]($fieldNameSym, _.$fieldName, $copier)
          """

      } else {
        q"""
          val ${Pat.Var.Term(fieldName)}: scaberry.core.Field[$srcTpe, $fieldType] =
            new scaberry.core.Field[$srcTpe, $fieldType]($fieldNameSym, _.$fieldName)"""
      }

      (fieldName, sbField)
    }.unzip
  }

}
