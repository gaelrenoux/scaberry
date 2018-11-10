package scaberry.tests.models

import scaberry.macros.berry

@berry
case class Sheep(color: String) {
  def this(other: Sheep) = this(other.color)
}