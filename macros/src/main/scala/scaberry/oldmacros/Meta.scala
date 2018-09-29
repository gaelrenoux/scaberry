package scaberry.oldmacros

import scaberry.core.CopyableField
import scaberry.oldmacros.impl.MetaMacroImpl

trait Meta[Source] {

  val fieldsMap: Map[Symbol, CopyableField[Source, _]]

  //val fieldsMapRo: Map[Symbol, Field[Source, _, NoCopier]]

  /** Returns a read-only field */
  //def fieldRo[Type](desc: Source => Type): Field[Source, Type, NoCopier] = macro MetaMacroImpl.fieldRo[Source, Type]

  def field[Type](desc: Source => Type): CopyableField[Source, Type] = macro MetaMacroImpl.field[Source, Type]

}
