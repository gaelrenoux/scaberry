package scalberto.macros.impl

import scalberto.core.Field
import scalberto.macros.Debug
import scalberto.macros.checks.Checkers

import scala.reflect.macros.whitebox

trait FieldHelper extends ClassStructureHelper with Debug with Checkers {

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

    val (copier, copierTpe) = copyParams.find { p =>
      val bool = p.name == name && typ =:= p.typeSignature.dealias.finalResultType
      bool
    } match {
      case None =>
        (q"scalberto.core.Field.NoCopier", tq"scalberto.core.Field.NoCopier.type")
      case Some(_) =>
        (q"{ (src: $srcTpe, a: $typ) => src.copy($name = a) }", tq"scalberto.core.Field.Copier[$srcTpe, $typ]")
    }

    debug(s"Writing field instance for $srcTpe.$name with $copierTpe")

    (
      q"$nameAsSymbol",
      q"new scalberto.core.Field[$srcTpe, $typ, $copierTpe]($nameAsSymbol, _.$name, $copier)"
    )
  }
}
