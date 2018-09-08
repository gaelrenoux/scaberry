package scalberto.macros

object FieldsMacro {

  def from[Source]: Fields[Source] = macro FieldsMacroImpl.fromCaseClass[Source]

  def fromConstructor[Source]: Fields[Source] = macro FieldsMacroImpl.fromPrimaryConstructor[Source]

  def fromPublic[Source]: Fields[Source] = macro FieldsMacroImpl.fromPublicFields[Source]
}