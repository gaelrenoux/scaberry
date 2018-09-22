package scalberto.tests.data

import scalberto.macros.{scaffield}

@scaffield
case class Cat(color: String, weight: Long = 1, name: Some[String]) extends Animal

object Cat {
  val catName = meta.fields.name
}