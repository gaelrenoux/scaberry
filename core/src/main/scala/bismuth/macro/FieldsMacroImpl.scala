package bismuth.`macro`

import bismuth.core.Fields

import scala.reflect.macros.whitebox

class FieldsMacroImpl(val c: whitebox.Context) {

  import c.universe._

  private lazy val debugEnabled =
    Option(System.getProperty("bismuth.macro.debug")).filterNot(_.isEmpty).map(_.toLowerCase).exists("true".equals)

  def debug(msg: => String): Unit = {
    if (debugEnabled) {
      c.info(c.enclosingPosition, msg, force = false)
    }
  }

  def constructorFields[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms { srcTpe =>
    val constructors = srcTpe.decl(c.universe.termNames.CONSTRUCTOR).alternatives.map(_.asMethod)
    val srcConstructor = constructors.find(_.isPrimaryConstructor).get
    srcConstructor.paramLists.flatten.map(_.asTerm)
  }

  def valsFields[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms { srcTpe =>
    val publics = srcTpe.members.map(_.asTerm).filter(s => s.isVal && s.isPublic)

    publics
  }

  private def withTerms[Source: c.WeakTypeTag](getTerms: Type => Iterable[TermSymbol]) = {
    val srcTag = implicitly[c.WeakTypeTag[Source]]
    val srcTpe = srcTag.tpe.dealias
    val terms = getTerms(srcTpe)
    val fieldsContent = terms.map(fieldToTree(_, srcTpe))

    val tree =
      q"""
        new bismuth.core.Fields[$srcTpe] {
           implicit val runtimeMirror = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
          ..$fieldsContent
        }
      """

    debug(showCode(tree))
    c.Expr[Fields[Source]](tree)
  }

  private def fieldToTree(sField: TermSymbol, srcTpe: Type) = {
    val name = sField.name
    val nameAsLiteral = Literal(Constant(name.toString))
    val typ = sField.typeSignature
    q"""
        val $name: bismuth.core.Field[$srcTpe, $typ] =
          new bismuth.core.Field.Impl[$srcTpe, $typ]($nameAsLiteral, _.$name)
      """
  }


}
