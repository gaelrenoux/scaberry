package scaberry.macros

import scala.collection.immutable.Seq
import scala.meta._

import scaberry.macros.helpers.{Unapply => U}

object SbFields {

  def namesAndDeclarations(clazz: Defn.Class, isCopyable: Boolean): (Seq[Term.Name], Seq[Defn]) = {
    val srcTpe = clazz.name
    val fieldTerms = clazz.ctor.paramss.flatten

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
