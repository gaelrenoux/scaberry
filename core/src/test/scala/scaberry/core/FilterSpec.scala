package scaberry.core

import org.scalatest.{FlatSpec, Matchers}

class FilterSpec extends FlatSpec with Matchers {

  val darwin: Person = Person("Charles Darwin", 50)
  val newton: Person = Person("Isaac Newton", 44)

  "None" should "validate any value" in {
    Filter.None.verify(42) should be(true)
    Filter.None.verify(false) should be(true)
    Filter.None.verify("") should be(true)
    Filter.None.verify(darwin) should be(true)
    Filter.None.verify(newton) should be(true)
  }

  "Value" should "validate the expected value" in {
    Filter.value(42).verify(42) should be(true)
    Filter.value("hello").verify("hello") should be(true)
    Filter.value(darwin).verify(darwin.copy()) should be(true)
  }

  it should "reject any unexpected value" in {
    Filter.value(42).verify(56) should be(false)
    Filter.value("hello").verify("world") should be(false)
    Filter.value(darwin).verify(newton) should be(false)
  }

  "Field filter" should "work with a value" in {
    Person.Fields.name.filterEq("Charles Darwin").verify(darwin) should be(true)
    Person.Fields.name.filterEq("Charles Darwin").verify(newton) should be(false)
  }

}
