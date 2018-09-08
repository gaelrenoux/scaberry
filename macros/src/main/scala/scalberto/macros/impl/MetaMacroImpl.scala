package scalberto.macros.impl

import scalberto.core.{CopyableField, Field}
import scalberto.core.Field.Copier
import scalberto.macros.{Debug, Fields, Meta}

import scala.reflect.macros.whitebox

/** Implementation of the fields macros */
class MetaMacroImpl(val c: whitebox.Context) extends ClassStructureHelper with FieldHelper with Debug {

  import c.universe._

  def fromCaseClass[Source: c.WeakTypeTag]: c.Expr[Meta[Source]] = withTerms(caseClassFields)

  private def withTerms[Source: c.WeakTypeTag](getTerms: Type => Iterable[TermSymbol]) = {
    val srcTag = implicitly[c.WeakTypeTag[Source]]
    val srcTpe = srcTag.tpe.dealias.finalResultType
    val terms = getTerms(srcTpe)
    val fields = fieldMap(terms, srcTpe)

    val tree =
      q"""
        new scalberto.macros.impl.MetaImpl[$srcTpe](
          $fields
        )
      """

    debug(showCode(tree))
    c.Expr[Meta[Source]](tree)
  }

  protected def fieldMap[Source: c.WeakTypeTag](terms: Iterable[TermSymbol], srcTpe: Type): c.Expr[Map[Symbol, CopyableField[Source, _]]] = {
    val copyMethodParams = findCopyMethod(srcTpe).map(_.paramLists.flatten.map(_.asTerm)).getOrElse(Nil)
    val fieldsContent = terms.map { sField =>
      val (sym, value) = fieldToValue(sField, srcTpe, copyMethodParams)
      q"$sym -> $value"
    }

    val tree = q""" Map(..$fieldsContent) """

    debug(showCode(tree))
    c.Expr[Map[Symbol, CopyableField[Source, _]]](tree)
  }

  def field[Source: c.WeakTypeTag, Type: c.WeakTypeTag](desc: c.Expr[Source => Type]): c.Expr[CopyableField[Source, Type]] = {
    ???
  }




}
