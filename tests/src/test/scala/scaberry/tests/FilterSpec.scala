package scaberry.tests

import org.scalatest.{FlatSpec, Matchers}
import scaberry.core.Filter
import scaberry.tests.models._

class FilterSpec extends FlatSpec with Matchers {

  //private val animalFields = Animal.meta.fields
  private val dogFields = Dog.meta.fields

  val inouk: Dog = Dog("Inouk", Some("Gael"))
  val rex: Dog = Dog("Rex", None, good = false, 2)

  "None" should "validate any value" in {
    Filter.None.verify(42) should be(true)
    Filter.None.verify(false) should be(true)
    Filter.None.verify("") should be(true)
    Filter.None.verify(inouk) should be(true)
    Filter.None.verify(rex) should be(true)
  }

  "Value" should "validate the expected value" in {
    Filter.value(42).verify(42) should be(true)
    Filter.value("hello").verify("hello") should be(true)
    Filter.value(inouk).verify(inouk.copy()) should be(true)
  }

  it should "reject any unexpected value" in {
    Filter.value(42).verify(56) should be(false)
    Filter.value("hello").verify("world") should be(false)
    Filter.value(inouk).verify(rex) should be(false)
  }

  "Field filter" should "work with a value" in {
    dogFields.name.filterEq("Inouk").verify(inouk) should be(true)
    dogFields.name.filterEq("Inouk").verify(rex) should be(false)
  }

}
