package scaberry.core.filter

import org.scalatest.{FlatSpec, Matchers}
import scaberry.core.Person

class FilterSpec extends FlatSpec with Matchers {

  val darwin: Person = Person("Charles Darwin", 50, bearded = true)
  val newton: Person = Person("Isaac Newton", 44)


  import CanCombineAnd._

  val f: Filter.Value[Long] = Filter.empty[Long].and(Filter.value(48L))
  val g: Filter.Value[Long] = Filter.value(48L).and(Filter.empty[Long])

  "None" should "validate any value" in {
    Filter.Empty.verify(42) should be(true)
    Filter.Empty.verify(false) should be(true)
    Filter.Empty.verify("") should be(true)
    Filter.Empty.verify(darwin) should be(true)
    Filter.Empty.verify(newton) should be(true)
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

  "Field" should "work with a value" in {
    val f = Filter.field(Person.Fields.name, Filter.value("Charles Darwin"))
    f.verify(darwin) should be(true)
    f.verify(newton) should be(false)
  }

  "Fields" should "work on two fields" in {
    val f1a = Filter.field(Person.Fields.name, Filter.value("Charles Darwin"))
    val f1b = Filter.field(Person.Fields.name, Filter.value("Isaac Newton"))
    val f2a = Filter.field(Person.Fields.age, Filter.value(50L))
    val f2b = Filter.field(Person.Fields.age, Filter.value(44L))

    Filter.fields(f1a, f2a).verify(darwin) should be(true)
    Filter.fields(f1b, f2a).verify(darwin) should be(false)
    Filter.fields(f1a, f2b).verify(darwin) should be(false)
  }

  it should "work on more than two fields" in {
    val f1a = Filter.field(Person.Fields.name, Filter.value("Charles Darwin"))
    val f1b = Filter.field(Person.Fields.name, Filter.value("Isaac Newton"))
    val f2a = Filter.field(Person.Fields.age, Filter.value(50L))
    val f2b = Filter.field(Person.Fields.age, Filter.value(44L))
    val f3a = Filter.field(Person.Fields.bearded, Filter.value(true))
    val f3b = Filter.field(Person.Fields.bearded, Filter.value(false))

    Filter.fields(f1a, f2a, f3a).verify(darwin) should be(true)
    Filter.fields(f1b, f2a, f3a).verify(darwin) should be(false)
    Filter.fields(f1a, f2b, f3a).verify(darwin) should be(false)
    Filter.fields(f1a, f2a, f3b).verify(darwin) should be(false)
  }

}
