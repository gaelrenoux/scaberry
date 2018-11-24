package scaberry.macrostests.models

import scaberry.macros.berry

@berry.fromPublicVals
class Cat(val name: String, val owner: Option[String], val childrenCount: Long = 0, startingWeight: Long = 1L) {

  def this(that: Cat) = this(that.name, that.owner, that.childrenCount, that.weight)

  val good: Boolean = owner.isEmpty

  lazy val owned: Boolean = owner.isEmpty

  def compliment: String = if (good) s"$name's a good dog!" else s"Bad $name, bad dog!"

  var weight: Long = startingWeight

}

object Cat