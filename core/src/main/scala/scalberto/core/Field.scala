package scalberto.core

import scalberto.core.Field.Copier

import scala.reflect.ClassTag


/** A field on a type. */
class Field[Source, Type, MaybeCopier](val name: Symbol,
                                       private val getter: Source => Type,
                                       private val copier: MaybeCopier = Field.NoCopier
                                      )(implicit
                                        val typeClassTag: ClassTag[Type],
                                        val sourceClassTag: ClassTag[Source]
                                      ) {

  type IsCopyable = MaybeCopier <:< Copier[Source, Type]
  type Application[T <: Source] = Field.Application[Source, Field[Source, Type, MaybeCopier], Type, T, MaybeCopier]

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

  type Readable[Source, Type] = Field[Source, Type, _]
  type Copyable[Source, Type] = Field[Source, Type, Copier[Source, Type]]

  object NoCopier


  /** Application of the field to a specific instance */
  class Application[Source, F <: Field[Source, Type, MaybeCopier], Type, Target <: Source, MaybeCopier](
                                                                                                         origin: F,
                                                                                                         target: Target
                                                                                                       ) {
    def get: Type = origin.getter(target)

    def copy(a: Type)(implicit ev: origin.IsCopyable): Source = ev(origin.copier)(target, a)
  }


}
