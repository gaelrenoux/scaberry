package scaberry.tests.data

import scaberry.core.CopyableField
import scaberry.macros.{CaseMeta, berry, berryProp}


@berry
case class Dog(
                @priority(10) color: String,
                weight: Long = 1,
                @label("Pet's name") @berryProp('label, "Pet's name") @berryProp('other, "Other") name: Some[String]
              ) extends Animal {
  val owner: String = "Unknown"

  private val genus = "canis" //unused but needed for test

  def otherOtherName: Option[String] = name

  var whatever2 = 0

  def unary2() = 42

  def parameterized2[A]: Nothing = ???
}

object Dog {

  /*object manualMeta extends CaseMeta[Dog] {

    object fields {
      val color: CopyableField[Dog, String] = new CopyableField[Dog, String]('color, _.color, (src, c) => src.copy(color = c))
      val weight: CopyableField[Dog, Long] = new CopyableField[Dog, Long]('weight, _.weight, (src, w) => src.copy(weight = w))
      val name: CopyableField[Dog, Some[String]] = new CopyableField[Dog, Some[String]]('name, _.name, (src, n) => src.copy(name = n))
    }

    val orderedFields: Seq[CopyableField[Dog, _]] = Seq(fields.color, fields.weight, fields.name)

    val a: Map[Symbol, Seq[CopyableField[Dog, _]]] = orderedFields.groupBy(_.name)
    val b = a.map { case (k, v) => (k, v.head) }

    val fieldsMap = orderedFields.groupBy(_.name).map { case (k, v) => (k, v.head) }.toMap
  }*/

}