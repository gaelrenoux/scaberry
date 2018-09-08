package scalberto.core

import scalberto.core.Field.{Copier, Setter}

import scala.reflect.ClassTag


/** A field on a type. */
class Field[Source, Type, MaybeCopier, MaybeSetter](val name: Symbol,
                                                    private val getter: Source => Type,
                                                    private val copier: MaybeCopier = Field.NoCopier,
                                                    private val setter: MaybeSetter = Field.NoSetter
                                                   )(implicit
                                                     val typeClassTag: ClassTag[Type],
                                                     val sourceClassTag: ClassTag[Source]
                                                   ) {

  type IsCopyable = MaybeCopier <:< Copier[Source, Type]
  type IsSettable = MaybeSetter <:< Setter[Source, Type]
  type Application[T <: Source] = Field.Application[Source, Field[Source, Type, MaybeCopier, MaybeSetter], Type, T, MaybeCopier, MaybeSetter]

  def apply[Target <: Source](target: Target): Application[Target] =
    new Field.Application(this, target)

  /** Returns a new filter on the source object which applies argument filter to this field. */
  def filter(f: Filter[Type]) =
    new Filter.Field[Source, Type](name, getter, f)

  /** Commodity method */
  def filterEq(v: Type): Filter.Field[Source, Type] =
    filter(Filter.value(v))

  /** Commodity method */
  def filterWith(f: Type => Boolean): Filter.Field[Source, Type] =
    filter(Filter.operation(f))

  /** Returns a new update on the source object which applies argument update to this field. */
  def update(u: Update[Type])(implicit ev: IsCopyable) =
    new Update.Field[Source, Type](name, getter, copier, u)

  /** Commodity method */
  def updateSet(v: Type)(implicit ev: IsCopyable): Update.Field[Source, Type] =
    update(Update.value(v))

  /** Commodity method */
  def updateWith(f: Type => Type)(implicit ev: IsCopyable): Update.Field[Source, Type] =
    update(Update.operation(f))
}

object Field {
  type Getter[Source, Type] = Source => Type
  type Copier[Source, Type] = (Source, Type) => Source
  type Setter[Source, Type] = (Source, Type) => Unit

  type Readable[Source, Type] = Field[Source, Type, _, _]
  type Copyable[Source, Type] = Field[Source, Type, Copier[Source, Type], _]
  type Settable[Source, Type] = Field[Source, Type, _, Setter[Source, Type]]

  object NoCopier

  object NoSetter


  /** Application of the field to a specific instance */
  class Application[Source, F <: Field[Source, Type, MaybeCopier, MaybeSetter], Type, Target <: Source, MaybeCopier, MaybeSetter](
                                                                                 origin: F,
                                                                                 target: Target
                                                                               ) {
    def get: Type = origin.getter(target)

    def copy(a: Type)(implicit ev: origin.IsCopyable): Source = ev(origin.copier)(target, a)

    def set(a: Type)(implicit ev: origin.IsSettable): Unit = ev(origin.setter)(target, a)

  }


}
