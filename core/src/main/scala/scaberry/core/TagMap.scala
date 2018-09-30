package scaberry.core

import scala.language.implicitConversions
import scala.reflect.ClassTag

/** A kind of Map associating values to types */
class TagMap private(wrapped: Map[ClassTag[_], Seq[_]]) {

  def get[A](implicit tag: ClassTag[A]): Option[A] = wrapped.get(tag).flatMap(_.headOption).map(_.asInstanceOf[A])

  def getList[A](implicit tag: ClassTag[A]): Seq[A] = wrapped.getOrElse(tag, Nil).map(_.asInstanceOf[A])

}

object TagMap {

  case class Entry[A](tag: ClassTag[A], value: A)

  object Entry {
    def apply[A](tag: ClassTag[A], value: A) = new Entry(tag, value)

    implicit def apply[A](value: A)(implicit tag: ClassTag[A]) = new Entry(tag, value)
  }

  def apply(entries: Entry[_]*): TagMap = {
    val map: Map[ClassTag[_], Seq[_]] = entries.groupBy(_.tag).map {
      case (k, v) => (k, v.map(_.value))
    }.toMap
    new TagMap(map)
  }

  def Empty = new TagMap(Map.empty)
}