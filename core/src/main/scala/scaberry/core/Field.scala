package scaberry.core

import scala.reflect.ClassTag


/** A field on a type. */
class Field[-Source, Type](val name: Symbol,
                           val getter: Source => Type,
                           val annotations: TagMap = TagMap.Empty
                          )(implicit
                            val typeClassTag: ClassTag[Type]
                          ) {

  def apply[Target <: Source](target: Target): Field.Application[Type] =
    new Field.Application[Type](getter(target))

  /** Returns a new filter on the source object which applies argument filter to this field. */
  def filter(f: Filter[Type]): Filter.FieldF[Source, Type] = Filter.field(this, f)

  /** Commodity method */
  def filterEq(v: Type): Filter.TreeFieldF[Source, Type] = Filter.field(this, Filter.value(v))

  /** Commodity method */
  def filterWith(f: Type => Boolean): Filter.FieldF[Source, Type] = Filter.field(this, Filter.operation(f))

  /** The same field but on a more specific class, or with a wider type. */
  def lifted[S <: Source, T >: Type : ClassTag]: Field[S, T] = new Field[S, T](name, getter)

}

class CopyableField[Source, Type](name: Symbol,
                                  getter: Source => Type,
                                  val copier: Copier[Source, Type],
                                  annotations: TagMap = TagMap.Empty
                                 )(implicit
                                   typeClassTag: ClassTag[Type]
                                 ) extends Field[Source, Type](name, getter, annotations) {

  override def apply[Target <: Source](target: Target): Field.CopyableApplication[Source, Type] =
    new Field.CopyableApplication(getter(target), copier(target, _))

  /** Returns a new update on the source object which applies argument update to this field. */
  def update(u: Update[Type]): Update.FieldU[Source, Type] = Update.field(this, u)

  /** Commodity method */
  def updateVal(v: Type): Update.TreeFieldU[Source, Type] = Update.field(this, Update.value(v))

  /** Commodity method */
  def updateWith(f: Type => Type): Update.FieldU[Source, Type] = Update.field(this, Update.operation(f))

}


object Field {

  /** Application of the field to a specific instance. */
  class Application[Type](wrapped: => Type) {
    def get: Type = wrapped
  }

  /** Application of the copyable field to a specific instance. */
  class CopyableApplication[Source, Type](wrapped: => Type, copier: Type => Source) extends Application[Type](wrapped) {
    def copy(a: Type): Source = copier(a)
  }

}