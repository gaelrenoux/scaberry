package scaberry.core

import org.scalatest.{FlatSpec, Matchers}

class FilterSpec extends FlatSpec with Matchers {

  val darwin: Person = Person("Charles Darwin", 50, bearded = true)
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

  "Tree filter" should "work on two fields" in {
    val f: Filter.TreeF[Person] = Person.Fields.name.filterEq("Charles Darwin") |@| Person.Fields.age.filterEq(50)
    f.verify(darwin) should be(true)
    f.verify(darwin.copy(age = 84)) should be(false)
    f.verify(darwin.copy(name = "Richard Dawkins")) should be(false)
    f.verify(newton) should be(false)
  }

  it should "work on more than two fields" in {
    val f: Filter.TreeF[Person] = Person.Fields.name.filterEq("Charles Darwin") |@| Person.Fields.age.filterEq(50) |@| Person.Fields.bearded.filterEq(true)
    f.verify(darwin) should be(true)
    f.verify(darwin.copy(age = 84)) should be(false)
    f.verify(darwin.copy(name = "Richard Dawkins")) should be(false)
    f.verify(newton) should be(false)
  }

}
