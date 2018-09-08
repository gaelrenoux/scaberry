package scalberto.macros.impl

import scalberto.macros.Debug
import scalberto.macros.checks.Checkers

import scala.reflect.macros.whitebox

trait ClassStructureHelper extends Debug with Checkers {

  val c: whitebox.Context

  import c.universe._

  /** Iterable on all fields of the case class. */
  def caseClassFields(tpe: Type): Iterable[TermSymbol] =
    checking(isCaseClass(tpe), s"Type $tpe is not a case class")(primaryConstructorParams(tpe))

  /** Iterable on all parameters of the primary constructor of the type. */
  def primaryConstructorParams(tpe: Type): Iterable[TermSymbol] = {
    val constructors = tpe.decl(c.universe.termNames.CONSTRUCTOR).alternatives.map(_.asMethod)
    val srcConstructor = constructors.find(_.isPrimaryConstructor).get
    srcConstructor.paramLists.flatten.map(_.asTerm)
  }

  /** Iterable on all public fields of the type. */
  def publicFields(tpe: Type): Iterable[TermSymbol] = tpe.members.map(_.asTerm).filter(s => s.isPublic && isField(s))


  /** A field is a val or a unary def. */
  def isField(s: TermSymbol): Boolean = s.isVal || isUnaryDef(s)


  /** A unary def has no parameter list (not even an empty one) and no type parameter. */
  def isUnaryDef(s: TermSymbol): Boolean = s.isMethod && {
    val sm = s.asMethod
    sm.paramLists.isEmpty && sm.typeParams.isEmpty
  }

  /** Finds the copy method of the type, if one exists. A copy method must be called "copy", return a subtype of the
    * type, and have default values for all parameters. */
  def findCopyMethod(srcTpe: Type): Option[MethodSymbol] = {
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

  def isCaseClass(tpe: Type): Boolean = isCaseClass(tpe.typeSymbol)

  def isCaseClass(sym: Symbol): Boolean = sym.isClass && sym.asClass.isCaseClass


}
