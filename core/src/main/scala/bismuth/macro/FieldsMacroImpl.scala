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


  def constructorFields[Source: c.WeakTypeTag]: c.Expr[Fields[Source]] = {
    val srcTag = implicitly[c.WeakTypeTag[Source]]
    val srcTpe = srcTag.tpe.dealias
    val srcConstructor = srcTpe.decl(c.universe.termNames.CONSTRUCTOR).asMethod

    val allFieldSymbols = srcConstructor.paramLists.flatten.map(_.asTerm)
    val fieldsContent = allFieldSymbols.map { fieldS =>
      val name = fieldS.name
      val nameAsLiteral = Literal(Constant(name.toString))
      val typ = fieldS.typeSignature
      q"""
        val $name: bismuth.core.Field[$srcTpe, $typ] = bismuth.runtime.ReflectField[$srcTpe, $typ]($nameAsLiteral)
      """
    }

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

}
