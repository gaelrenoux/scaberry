package scalberto.macros

import scalberto.macros.impl.FieldsMacroImpl

object FieldsMacro {

  def from[Source]: Fields[Source] = macro FieldsMacroImpl.fromCaseClass[Source]

  def fromConstructor[Source]: Fields[Source] = macro FieldsMacroImpl.fromPrimaryConstructor[Source]

  def fromPublic[Source]: Fields[Source] = macro FieldsMacroImpl.fromPublicFields[Source]
}