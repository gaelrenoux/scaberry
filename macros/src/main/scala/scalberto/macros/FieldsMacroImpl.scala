package scalberto.macros

import scalberto.core.Fields

import scala.reflect.macros.whitebox

/** Implementation of the fields macros */
class FieldsMacroImpl(val c: whitebox.Context) extends Helpers with Debug {

  import c.universe._

  def fromCaseClass[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms(caseClassFields)

  def fromPrimaryConstructor[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms(primaryConstructorParams)

  def fromPublicFields[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms(publicFields)

  private def withTerms[Source: c.WeakTypeTag](getTerms: Type => Iterable[TermSymbol]) = {
    val srcTag = implicitly[c.WeakTypeTag[Source]]
    val srcTpe = srcTag.tpe.dealias.finalResultType
    val terms = getTerms(srcTpe)
    val copyMethodParams = findCopyMethod(srcTpe).map(_.paramLists.flatten.map(_.asTerm)).getOrElse(Nil)
    val fieldsContent = terms.map(fieldToTree(_, srcTpe, copyMethodParams))

    val tree =
      q"""
        new scalberto.core.Fields[$srcTpe] {
          ..$fieldsContent
        }
      """

    debug(showCode(tree))
    c.Expr[Fields[Source]](tree)
  }

  /** For a single field, returns a Tree of the associated Field object. */
  private def fieldToTree(sField: TermSymbol, srcTpe: Type, copyParams: List[TermSymbol]): Tree = {
    val name = sField.name
    //TODO Handle Symbol directly
    val nameAsLiteral = Literal(Constant(name.toString))
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

    val (setter, setterTpe) = (q"scalberto.core.Field.NoSetter", tq"scalberto.core.Field.NoSetter.type")

    debug(s"Writing field instance for $srcTpe.$name with $copierTpe and $setterTpe")

    q"""
        val $name = new scalberto.core.Field[$srcTpe, $typ, $copierTpe, $setterTpe](Symbol($nameAsLiteral), _.$name, $copier, $setter)
    """
  }


}
