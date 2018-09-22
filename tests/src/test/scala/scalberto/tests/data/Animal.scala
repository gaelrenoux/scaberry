package scalberto.tests.data

import scalberto.macros.FieldsMacro


trait Animal {

  val weight: Long
  val color: String
  val name: Option[String]

  lazy val itself: Animal = this

  protected val animate = true

  def otherName: Option[String] = name

  var whatever = 0

  def unary() = 42

  def parameterized[A]: Nothing = ???
}

object Animal {
  val publicFields = FieldsMacro.fromPublic[Animal]
}