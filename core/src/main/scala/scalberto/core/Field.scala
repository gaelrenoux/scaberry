package scalberto.core

import scalberto.core.Field.Copier

import scala.reflect.ClassTag


/** A field on a type. */
class Field[Source, Type](val name: Symbol,
                          private val getter: Source => Type
                         )(implicit
                           val typeClassTag: ClassTag[Type],
                           val sourceClassTag: ClassTag[Source]
                         ) {

  private type Application[T <: Source] = Field.Application[Source, Type, Field[Source, Type], T]

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
}

class CopyableField[Source, Type](name: Symbol,
                                  getter: Source => Type,
                                  private[core] val copier: Copier[Source, Type]
                                 )(implicit
                                   typeClassTag: ClassTag[Type],
                                   sourceClassTag: ClassTag[Source]
                                 ) extends Field[Source, Type](name, getter) {

  private type Application[T <: Source] = Field.CopyableApplication[Source, Type, CopyableField[Source, Type], T]

  override def apply[Target <: Source](target: Target): Application[Target] =
    new Field.CopyableApplication(this, target)

  /** Returns a new update on the source object which applies argument update to this field. */
  def update(u: Update[Type]) =
    new Update.Field[Source, Type](name, getter, copier, u)

  /** Commodity method */
  def updateSet(v: Type): Update.Field[Source, Type] =
    update(Update.value(v))

  /** Commodity method */
  def updateWith(f: Type => Type): Update.Field[Source, Type] =
    update(Update.operation(f))

}


object Field {
  type Getter[-Source, +Type] = Source => Type
  type Copier[Source, -Type] = (Source, Type) => Source

  /** Application of the field to a specific instance */
  class Application[Source, Type, +F <: Field[Source, Type], Target <: Source]
  (origin: F, target: Target) {
    def get: Type = origin.getter(target)
  }

  /** Application of the copyable field to a specific instance */
  class CopyableApplication[Source, Type, +F <: CopyableField[Source, Type], Target <: Source]
  (origin: F, target: Target) extends Application[Source, Type, F, Target](origin, target) {

    def copy(a: Type): Source = origin.copier(target, a)

  }

}