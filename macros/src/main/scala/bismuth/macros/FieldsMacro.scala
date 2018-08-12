package bismuth.macros

import bismuth.core.Fields

object FieldsMacro {

  def fromConstructor[Source]: Fields[Source] = macro FieldsMacroImpl.constructorFields[Source]

  def fromPublic[Source]: Fields[Source] = macro FieldsMacroImpl.publicFields[Source]
}