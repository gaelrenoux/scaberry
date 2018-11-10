package scaberry.tests.models

import scaberry.macros.{berry, berryProp}


@berry
case class Wolf(
                 @priority(10) color: String,
                 @label("True name") @berryProp('label, "True name") @berryProp('other, "Other") name: Some[String],
                 @label("Children") @label("Cubs") childrenCount: Long = 0
               )