package scaberry.tests.models


import scala.annotation.StaticAnnotation

/** Not a case class */
class priority(val level: Int = 0) extends StaticAnnotation

/** A case class */
case class label(value: String) extends StaticAnnotation