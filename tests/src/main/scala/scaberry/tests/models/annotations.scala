package scaberry.tests.models


import scaberry.macros.BerryProperty

import scala.annotation.StaticAnnotation

/** Not a case class */
class priority(val level: Int = 0) extends StaticAnnotation

/** A case class */
case class label(value: String) extends StaticAnnotation

/** An extension of BerryProperty */
case class text(override val value: String) extends BerryProperty[String] {
  override val name = 'text
}