package scalberto.tests.data

import scalberto.macros.{FieldsMacro, MetaMacro}

case class Dog(color: String, weight: Long = 1, name: Some[String]) extends Animal {
  val owner: String = "Unknown"

  private val genus = "canis" //unused but needed for test

  def otherOtherName: Option[String] = name

  var whatever2 = 0

  def unary2() = 42

  def parameterized2[A]: Nothing = ???

  def this(other: Dog) = this(other.color, other.weight, other.name)
}

object Dog {
  val fields = FieldsMacro.from[Dog]
  val publicFields = FieldsMacro.fromPublic[Dog]
  val constructorFields = FieldsMacro.fromConstructor[Dog]

  val meta = MetaMacro.from[Dog]
}