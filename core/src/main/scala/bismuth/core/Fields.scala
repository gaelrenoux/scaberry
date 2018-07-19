package bismuth.core

import bismuth.`macro`.FieldsMacroImpl

trait Fields[-Source] {
  //def all: Seq[Field[Source, _]]
}

object Fields {

  def fromConstructor[Source]: Fields[Source] = macro FieldsMacroImpl.constructorFields[Source]

  def fromPublic[Source]: Fields[Source] = macro FieldsMacroImpl.publicFields[Source]
}