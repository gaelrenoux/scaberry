package scaberry.macrostests.models

import scaberry.macros.berry

@berry
case class Wolf(color: String) {
  def this(other: Wolf) = this(other.color)
}
