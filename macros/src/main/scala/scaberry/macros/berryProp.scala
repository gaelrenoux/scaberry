package scaberry.macros

import scala.annotation.StaticAnnotation

case class berryProp(name: Symbol, value: String) extends StaticAnnotation
