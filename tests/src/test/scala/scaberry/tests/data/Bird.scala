package scaberry.tests.data

import scaberry.macros.berry

@berry.fromPublicVals
class Bird(origin: String, val color: String) {
  val name: String = "Tweety"
}