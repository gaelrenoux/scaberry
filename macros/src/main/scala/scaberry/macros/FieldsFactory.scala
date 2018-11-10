package scaberry.macros

import scaberry.macros.helpers.{Unapply => U}

import scala.collection.immutable.Seq
import scala.meta._


object FieldsFactory {

  /** @return all terms from the primary constructor */
  def primaryConstructorTerms(clazz: Defn.Class): Seq[Term.Param] = {
    clazz.ctor.paramss.flatten
  }

  /** @return all public val terms */
  def publicValTerms(clazz: Defn.Class): Seq[Term.Param] = {
    clazz.templ.children.flatMap {
      case Defn.Val(mods, patterns, typeOption, content) if isPublic(mods) =>
        val typ = typeOption.getOrElse(Type.Name("Any"))
        expandTuples(patterns).map(p => (mods, p, typ))
      case _ => Seq.empty
    }.collect {
      case (mods, patVar: Pat.Var, typ) =>
        val varName = patVar.children.head.asInstanceOf[Term.Name]
        Term.Param(mods, varName, Some(typ), None)
    }
  }

  /** @return true if no mod in the Seq reduces the visibility from public */
  private def isPublic(mods: Seq[Mod]) = {
    !mods.exists {
      case Mod.Private(_) => true
      case Mod.Protected(_) => true
      case _ => false
    }
  }

  /** Flattens the Seq by expanding tuples */
  private def expandTuples(s: Seq[Pat]): Seq[Pat.Var] = {
    s.foldLeft(Seq[Pat.Var]()) {
      case (acc, v: Pat.Var) => acc :+ v
      case (acc, Pat.Tuple(list)) => acc ++ expandTuples(list)
    }
  }

  /** @return All field names, and the associated Field objects */
  def namesAndDeclarations(clazz: Defn.Class, isCopyable: Boolean, fieldTerms: Seq[Term.Param]): (Seq[Term.Name], Seq[Defn]) = {
    val srcTpe = clazz.name

    fieldTerms.map { fieldTerm =>
      val fieldName = Term.Name(fieldTerm.name.value)
      val fieldType = fieldTerm.decltpe.get.asInstanceOf[Type]
      val fieldNameSym = Lit.Symbol(scala.Symbol(fieldName.value))

      val fieldProps = fieldTerm.mods.collect {
        case BerryProp(name, value) => q"""val ${Pat.Var.Term(Term.Name(name.name))} = ${Lit.String(value)}"""
      }

      val annotations = fieldTerm.mods.collect {
        case U.Annotation(name, args) => q"new ${Ctor.Name(name)}(..$args)"
      }

      val sbField = if (isCopyable) {
        val copier = q"(src: $srcTpe, a: $fieldType) => src.copy($fieldName = a)"
        q"""
          object $fieldName extends scaberry.core.CopyableField[$srcTpe, $fieldType](
            $fieldNameSym,
            _.$fieldName,
            $copier,
            scaberry.core.TagMap(..$annotations)
          ) {
            ..$fieldProps
          }
          """

      } else {
        q"""
          object $fieldName extends scaberry.core.Field[$srcTpe, $fieldType](
            $fieldNameSym,
            _.$fieldName,
            scaberry.core.TagMap(..$annotations)
          ) {
            ..$fieldProps
          }
          """
      }

      (fieldName, sbField)
    }.unzip
  }

  object BerryProp {
    private val BerryPropName = classOf[berryProp].getSimpleName

    def unapply(tree: Tree): Option[(scala.Symbol, String)] = tree match {
      case U.Annotation(BerryPropName, Seq(U.Symbol(name), Lit(value: String))) => Some((name, value))
      case _ => None
    }
  }

}
