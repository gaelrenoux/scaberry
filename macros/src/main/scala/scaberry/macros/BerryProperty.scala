package scaberry.macros

import scala.annotation.StaticAnnotation

//TODO not functional for now
trait BerryProperty[A] extends StaticAnnotation {
  val name: Symbol
  val value: A
}
