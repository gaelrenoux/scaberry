package scalberto.macros.impl

import scalberto.macros.checks.Checkers

import scala.reflect.macros.whitebox

trait FieldHelper extends ClassStructureHelper with Checkers {

  val c: whitebox.Context

  import c.universe._

  /** For a single field, returns a Tree of the associated Field declaration. */
  protected def fieldToDeclaration(sField: TermSymbol, srcTpe: Type, copyParams: List[TermSymbol]): Tree = {
    val name = sField.name
    val valueTree = fieldToValue(sField, srcTpe, copyParams)._2
    q"""val $name = $valueTree"""
  }

  /** For a single field, returns a couple of Trees: a symbol of the name of the field, and the associated Field value. */
  protected def fieldToValue(sField: TermSymbol, srcTpe: Type, copyParams: List[TermSymbol]): (Tree, Tree) = {
    val name = sField.name
    val nameAsSymbol = Symbol(name.toString)
    val typ = sField.typeSignature.dealias.finalResultType

    val copyable = copyParams.exists { p =>
      p.name == name && p.typeSignature.dealias.finalResultType =:= typ
    }

    val field =
      if (copyable) {
        val copier = q"{ (src: $srcTpe, a: $typ) => src.copy($name = a) }"
        q"new scalberto.core.CopyableField[$srcTpe, $typ]($nameAsSymbol, _.$name, $copier)"
      } else {
        q"new scalberto.core.Field[$srcTpe, $typ]($nameAsSymbol, _.$name)"
      }

    (q"$nameAsSymbol", field)
  }
}
