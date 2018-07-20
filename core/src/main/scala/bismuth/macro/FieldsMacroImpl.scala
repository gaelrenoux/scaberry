package bismuth.`macro`

import bismuth.core.Fields

import scala.reflect.macros.whitebox

class FieldsMacroImpl(val c: whitebox.Context) {

  import c.universe._

  private lazy val debugEnabled = true
    //Option(System.getProperty("bismuth.macro.debug")).filterNot(_.isEmpty).map(_.toLowerCase).exists("true".equals)

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
    val srcTpe = srcTag.tpe.dealias.finalResultType
    val terms = getTerms(srcTpe)
    val copyMethodParams = findCopy(srcTpe).map(_.paramLists.flatten.map(_.asTerm)).getOrElse(Nil)
    val fieldsContent = terms.map(fieldToTree(_, srcTpe, copyMethodParams))

    val tree =
      q"""
        new bismuth.core.Fields[$srcTpe] {
           implicit val runtimeMirror = scala.reflect.runtime.universe.runtimeMirror(getClass.getClassLoader)
          ..$fieldsContent
        }
      """

    //debug(showCode(tree))
    c.Expr[Fields[Source]](tree)
  }

  private def fieldToTree(sField: TermSymbol, srcTpe: Type, copyParams: List[TermSymbol]): Tree = {
    val name = sField.name
    val nameAsLiteral = Literal(Constant(name.toString))
    val typ = sField.typeSignature.dealias.finalResultType

    val (copier, copierTpe) = copyParams.find { p =>
      val bool = p.name == name && typ =:= p.typeSignature.dealias.finalResultType
      bool
    } match {
      case None =>
        (q"bismuth.core.Field.NoCopier", tq"bismuth.core.Field.NoCopier.type")
      case Some(_) =>
        (q"{ (src: $srcTpe, a: $typ) => src.copy($name = a) }", tq"bismuth.core.Field.Copier[$srcTpe, $typ]")
    }

    val (setter, setterTpe) = (q"bismuth.core.Field.NoSetter", tq"bismuth.core.Field.NoSetter.type")

    debug(s"Writing field instance for $srcTpe.$name with $copierTpe and $setterTpe")

    q"""
        val $name = new bismuth.core.Field[$srcTpe, $typ, $copierTpe, $setterTpe]($nameAsLiteral, _.$name, $copier, $setter)
    """
  }

  private def findCopy(srcTpe: Type): Option[MethodSymbol] = {
    val memberCopy = srcTpe.member(TermName("copy"))
    for {
      copy <- if (memberCopy.isMethod) Some(memberCopy.asMethod) else None
      /* Check return type: should be a subclass of the type */
      _ <- if (copy.returnType <:< srcTpe) Some(copy) else None
      /* Check that all parameters have default values */
      params = copy.paramLists.flatten
      _ <- if (params.forall(_.asTerm.isParamWithDefault)) Some(copy) else None
    } yield copy
  }


}
