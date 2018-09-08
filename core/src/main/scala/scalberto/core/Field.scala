package scalberto.core

import scala.reflect.ClassTag


/** A field on a type. */
class Field[Source, Type, +MaybeCopier, +MaybeSetter](val name: Symbol,
                                                    getter: Source => Type,
                                                    copier: MaybeCopier = Field.NoCopier,
                                                    setter: MaybeSetter = Field.NoSetter
                                                   )(implicit
                                                     val typeClassTag: ClassTag[Type],
                                                     val sourceClassTag: ClassTag[Source]
                                                   ) {

  def apply[Target <: Source](target: Target): Field.Application[Source, Type, Target, MaybeCopier, MaybeSetter] =
    new Field.Application(target, getter, copier, setter)

  def filter(f: Filter[Type]) = new Filter.Field[Source, Type](name, getter, f)

  /** Commodity method */
  def filterEq(v: Type) = new Filter.Field[Source, Type](name, getter, Filter.value(v))

  /** Commodity method */
  def filterWith(f: Type => Boolean) = new Filter.Field[Source, Type](name, getter, Filter.operation(f))
}

object Field {
  type Copier[Source, Type] = (Source, Type) => Source
  type Setter[Source, Type] = (Source, Type) => Unit

  type Readable[Source, Type] = Field[Source, Type, _, _]
  type Copyable[Source, Type] = Field[Source, Type, Copier[Source, Type], _]
  type Settable[Source, Type] = Field[Source, Type, _, Setter[Source, Type]]

  object NoCopier
  object NoSetter


  /** Application of the field to a specific instance */
  class Application[Source, Type, Target <: Source, +MaybeCopier, +MaybeSetter](
                                                                                 protected val target: Target,
                                                                                 getter: Source => Type,
                                                                                 copier: MaybeCopier = None,
                                                                                 setter: MaybeSetter = None
                                                                             ) {
    def get: Type = getter(target)

    def copy(a: Type)(implicit ev: MaybeCopier <:< Copier[Source, Type]): Source = ev(copier)(target, a)

    def set(a: Type)(implicit ev: MaybeSetter <:< Setter[Source, Type]): Unit = ev(setter)(target, a)

  }


}
