package scalberto.tests

import Models._
import org.scalatest.{FlatSpec, Matchers}
import scalberto.core.Filter
import scalberto.macros.FieldsMacro

class FilterSpec extends FlatSpec with Matchers with Helpers {

  private val animalFields = FieldsMacro.fromPublic[Animal]
  private val dogFields = FieldsMacro.from[Dog]

  val casimir: Animal = new Animal {
    override val weight: Long = 42L
    override val color: String = "orange"
    override val name: Option[String] = Some("Casimir")
  }

  val rex: Dog = Dog("brown", 9L, Some("Rex"))

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
    force.rf[Animal, Option[String]](animalFields.name).filterEq(Some("Casimir")).verify(casimir) should be(true)
    force.rf[Animal, Option[String]](animalFields.name).filterEq(Some("Rex")).verify(casimir) should be(false)
  }
}
