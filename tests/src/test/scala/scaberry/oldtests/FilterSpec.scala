package scaberry.oldtests

import org.scalatest.{FlatSpec, Matchers}
import scaberry.core.Filter
import scaberry.oldmacros.FieldsMacro

class FilterSpec extends FlatSpec with Matchers with Helpers {

  private val animalFields = FieldsMacro.fromPublic[data.Animal]
  private val dogFields = FieldsMacro.from[data.Dog]

  val casimir: data.Animal = new data.Animal {
    override val weight: Long = 42L
    override val color: String = "orange"
    override val name: Option[String] = Some("Casimir")
  }

  val rex: data.Dog = data.Dog("brown", 9L, Some("Rex"))
  val littleRex: data.Dog = rex.copy(weight = 1L)
  val popa: data.Dog = data.Dog("white", 9L, Some("Popa"))

  "None" should "verify any value" in {
    Filter.None.verify(42) should be(true)
    Filter.None.verify(false) should be(true)
    Filter.None.verify("") should be(true)
    Filter.None.verify(casimir) should be(true)
  }

  "Value" should "verify the expected value" in {
    Filter.value(42).verify(42) should be(true)
    Filter.value("hello").verify("hello") should be(true)
    Filter.value(rex).verify(rex.copy()) should be(true)
  }

  it should "reject any unexpected value" in {
    Filter.value(42).verify(56) should be(false)
    Filter.value("hello").verify("world") should be(false)
    Filter.value(casimir).verify(rex) should be(false)
  }

  "Field filter" should "work with a value" in {
    force.rf[data.Animal, Option[String]](animalFields.name).filterEq(Some("Casimir")).verify(casimir) should be(true)
    force.rf[data.Animal, Option[String]](animalFields.name).filterEq(Some("Rex")).verify(casimir) should be(false)
  }

  it should "work with an operation" in {
    force.rf[data.Animal, Option[String]](animalFields.name).filterWith(_.isDefined).verify(casimir) should be(true)
    force.rf[data.Animal, Option[String]](animalFields.name).filterWith(_.isEmpty).verify(casimir) should be(false)
  }

  it should "work with a subfield" in {
    val f1 = force.rf[data.Animal, Option[String]](animalFields.name).filterEq(Some("Casimir"))
    val f = force.rf[data.Animal, data.Animal](animalFields.itself).filter(f1)
    f.verify(casimir) should be(true)
    f.verify(rex) should be(false)
  }

  "Composed filter" should "work with basic filters" in {
    val f = Filter.operation[String](_.size > 4) |@| Filter.operation[String](_.startsWith("H"))
    f.verify("Hello") should be(true)
    f.verify("Hi") should be(false)
    f.verify("Bonjour") should be(false)
  }

  it should "work with field filters" in {
    val f = force.rf[data.Animal, Long](animalFields.weight).filterEq(9) |@|
      force.rf[data.Animal, String](animalFields.color).filterEq("brown")
    f.verify(rex) should be(true)
    f.verify(littleRex) should be(false)
    f.verify(popa) should be(false)
  }
}
