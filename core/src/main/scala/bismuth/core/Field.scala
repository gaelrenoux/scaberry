package bismuth.core

import bismuth.core

import scala.reflect.ClassTag



class Field[Source, Type, MaybeCopier, MaybeSetter](val name: String,
                                                    getter: Source => Type,
                                                    copier: MaybeCopier = None,
                                                    setter: MaybeSetter = None
                                                   )(implicit
                                                     val typeClassTag: ClassTag[Type],
                                                     val sourceClassTag: ClassTag[Source]
                                                   ) {

  def apply[Target <: Source](target: Target): FieldApplication[Source, Type, Target, MaybeCopier, MaybeSetter] =
    new core.FieldApplication(target, getter, copier, setter)
}

object Field {
  type Copier[Source, Type] = (Source, Type) => Source
  type Setter[Source, Type] = (Source, Type) => Unit

  type Readable[Source, Type] = Field[Source, Type, _, _]
  type Copyable[Source, Type] = Field[Source, Type, Copier[Source, Type], _]
  type Settable[Source, Type] = Field[Source, Type, _, Setter[Source, Type]]








}
