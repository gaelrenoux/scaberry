package scaberry.tests.models

import scaberry.macros.berry

@berry
case class Dog(name: String, owner: Option[String], good: Boolean = true, childrenCount: Long = 0) {

  val fullName: String = s"${owner.getOrElse("No one")}'s $name"

  lazy val owned: Boolean = owner.isEmpty

  def compliment: String = if (good) s"$name's a good dog!" else s"Bad $name, bad dog!"

  var weight: Long = 3L

}
