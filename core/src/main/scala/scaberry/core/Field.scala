package scaberry.core

import scaberry.core.Field.Copier

import scala.reflect.ClassTag


/** A field on a type. */
class Field[-Source, Type](val name: Symbol,
                           private val getter: Source => Type
                          )(implicit
                            val typeClassTag: ClassTag[Type]
                          ) {

  def apply[Target <: Source](target: Target): Field.Application[Type] =
    new Field.Application[Type](getter(target))

  /** Returns a new filter on the source object which applies argument filter to this field. */
  def filter(f: Filter[Type]): Filter.Field[Source, Type] =
    new Filter.Field[Source, Type](name, getter, f)

  /** Commodity method */
  def filterEq(v: Type): Filter.Field[Source, Type] =
    filter(Filter.value(v))

  /** Commodity method */
  def filterWith(f: Type => Boolean): Filter.Field[Source, Type] =
    filter(Filter.operation(f))

  /** The same field but on a more specific class, or with a wider type. */
  def lifted[S <: Source, T >: Type : ClassTag]: Field[S, T] = new Field[S, T](name, getter)

}

class CopyableField[Source, Type](name: Symbol,
                                  getter: Source => Type,
                                  private[core] val copier: Copier[Source, Type]
                                 )(implicit
                                   typeClassTag: ClassTag[Type]
                                 ) extends Field[Source, Type](name, getter) {

  override def apply[Target <: Source](target: Target): Field.CopyableApplication[Source, Type] =
    new Field.CopyableApplication(getter(target), copier(target, _))

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
  type Copier[Source, -Type] = Function2[Source, Type, Source]

  /** Application of the field to a specific instance. */
  class Application[Type](wrapped: => Type) {
    def get: Type = wrapped
  }

  /** Application of the copyable field to a specific instance. */
  class CopyableApplication[Source, Type](wrapped: => Type, copier: Type => Source) extends Application[Type](wrapped) {
    def copy(a: Type): Source = copier(a)
  }

}