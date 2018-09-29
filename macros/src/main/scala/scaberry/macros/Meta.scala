package scaberry.macros

import scaberry.core.{CopyableField, Field}

trait Meta[Source] {

  val orderedFields: Seq[Field[Source, _]]

  val fieldsMap: Map[Symbol, Field[Source, _]]

}

trait CopyableMeta[Source] extends Meta[Source] {

  val orderedFields: Seq[CopyableField[Source, _]]

  val fieldsMap: Map[Symbol, CopyableField[Source, _]]

}