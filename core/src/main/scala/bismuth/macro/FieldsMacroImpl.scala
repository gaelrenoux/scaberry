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

  def publicFields[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = withTerms { srcTpe =>
    srcTpe.members.map(_.asTerm).filter(s => s.isPublic && isField(s))
  }

  /* Keep only vals and nullary methods */
  private def isField(s: TermSymbol) = {
    s.isVal || (s.isMethod && {
      val sm = s.asMethod
      sm.paramLists.isEmpty && sm.typeParams.isEmpty
    })
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
        val $name: bismuth.core.Field[$srcTpe, $typ, Any, Any] =
          new bismuth.core.Field[$srcTpe, $typ, Any, Any]($nameAsLiteral, _.$name)
      """
  }


}
