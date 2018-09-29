package scaberry.macros

import scala.annotation.StaticAnnotation

class berryProp(val name: Symbol, val value: String) extends StaticAnnotation
