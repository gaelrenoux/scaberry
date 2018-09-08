package scalberto.macros

import scalberto.core.Field
import scalberto.core.Field.Copier
import scalberto.macros.impl.MetaMacroImpl

trait Meta[Source] {

  val fieldsMap: Map[Symbol, Field[Source, _, Copier[Source, _]]]

  //val fieldsMapRo: Map[Symbol, Field[Source, _, NoCopier]]

  /** Returns a read-only field */
  //def fieldRo[Type](desc: Source => Type): Field[Source, Type, NoCopier] = macro MetaMacroImpl.fieldRo[Source, Type]

  //def field[Type](desc: Source => Type): Field[Source, Type, Copier[Source, Type]] = macro MetaMacroImpl.field[Source, Type]

}
