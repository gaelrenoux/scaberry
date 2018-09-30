package scaberry.tests.data

import scaberry.macros.berry

@berry
case class Sheep(color: String) {
  def this(other: Sheep) = this(other.color)
}