package scaberry.macros

import scala.meta._


object MetaFactory {

  def meta(clazz: Defn.Class, berryName: String, fromVals: Boolean = false): Defn.Object = {
    val isCopyable = clazz.mods.exists(_.isInstanceOf[Mod.Case])
    val srcTpe = clazz.name
    val fieldTerms = if (fromVals) FieldsFactory.publicValTerms(clazz) else FieldsFactory.primaryConstructorTerms(clazz)
    val (sbNames, sbDeclarations) =
      FieldsFactory.namesAndDeclarations(clazz, isCopyable, fieldTerms)

    val qualifiedSbNames = sbNames.map(n => q"fields.$n")
    val metaObjectName = Term.Name(berryName)

    if (isCopyable)
      q"""
          object $metaObjectName extends scaberry.macros.CaseMeta[$srcTpe] {
            object fields {
              ..$sbDeclarations
            }

            val orderedFields: Seq[scaberry.core.CopyableField[$srcTpe, _]] =
              Seq(..$qualifiedSbNames)

            val fieldsMap: Map[scala.Symbol, scaberry.core.CopyableField[$srcTpe, _]] =
              orderedFields.groupBy(_.name).map { case (k, v) => (k, v.head) }.toMap
          }
        """
    else
      q"""
          object $metaObjectName extends scaberry.macros.Meta[$srcTpe] {
            object fields {
              ..$sbDeclarations
            }

            val orderedFields: Seq[scaberry.core.Field[$srcTpe, _]] =
              Seq(..$qualifiedSbNames)

            val fieldsMap: Map[scala.Symbol, scaberry.core.Field[$srcTpe, _]] =
              orderedFields.groupBy(_.name).map { case (k, v) => (k, v.head) }.toMap
          }
        """
  }

}
