package scaberry.oldmacros

import scaberry.oldmacros.impl.MetaMacroImpl

object MetaMacro {

  def from[Source]: Meta[Source] = macro MetaMacroImpl.fromCaseClass[Source]

}
