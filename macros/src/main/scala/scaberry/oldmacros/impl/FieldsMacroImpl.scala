package scaberry.oldmacros.impl

import scaberry.oldmacros.{Debug, Fields}

import scala.reflect.macros.whitebox

/** Implementation of the fields macros */
class FieldsMacroImpl(val c: whitebox.Context) extends ClassStructureHelper with FieldHelper with Debug {

  import c.universe._

  def fromCaseClass[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms(caseClassFields)

  def fromPrimaryConstructor[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms(primaryConstructorParams)

  def fromPublicFields[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms(publicFields)

  private def withTerms[Source: c.WeakTypeTag](getTerms: Type => Iterable[TermSymbol]) = {
    val srcTag = implicitly[c.WeakTypeTag[Source]]
    val srcTpe = srcTag.tpe.dealias.finalResultType
    val terms = getTerms(srcTpe)
    val copyMethodParams = findCopyMethod(srcTpe).map(_.paramLists.flatten.map(_.asTerm)).getOrElse(Nil)
    val fieldsContent = terms.map(fieldToDeclaration(_, srcTpe, copyMethodParams))

    val tree =
      q"""
        new scaberry.oldmacros.Fields[$srcTpe] {
          ..$fieldsContent
        }
      """

    debug(showCode(tree))
    c.Expr[Fields[Source]](tree)
  }
}
