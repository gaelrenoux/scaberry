package scaberry.tests.models

import scaberry.macros.{berry, berryProp}

@berry
case class Dog(
                @label("Nom") name: String,
                @label("Maître") @berryProp('label, "Master") owner: Option[String],
                @priority(10) @priority(12) good: Boolean = true,
                @berryProp('masked, "yes") @berryProp('label, "no") childrenCount: Long = 0
              ) {

  val fullName: String = s"${owner.getOrElse("No one")}'s $name"

  lazy val owned: Boolean = owner.isEmpty

  def compliment: String = if (good) s"$name's a good dog!" else s"Bad $name, bad dog!"

  var weight: Long = 3L

}
