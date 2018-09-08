package scalberto.macros

import scalberto.macros.impl.MetaMacroImpl

object MetaMacro {

  def from[Source]: Meta[Source] = macro MetaMacroImpl.fromCaseClass[Source]

}
