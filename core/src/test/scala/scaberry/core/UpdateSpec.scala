package scaberry.core

import org.scalatest.{FlatSpec, Matchers}

class UpdateSpec extends FlatSpec with Matchers {

  val darwin: Person = Person("Charles Darwin", 50, bearded = true)
  val newton: Person = Person("Isaac Newton", 44)

  "identity" should "not change any value" in {
    Update.identity[Person].update(darwin) should be(darwin)
    Update.identity[Person].update(newton) should be(newton)
  }

  "value" should "set the expected value" in {
    Update.value(42).update(56) should be(42)
    Update.value("hello").update("goodbye") should be("hello")
    Update.value(darwin).update(newton) should be(darwin)
  }

  "operation" should "set the expected value" in {
    Update.operation[Int](_ * 2).update(21) should be(42)
    Update.operation[String](_.reverse).update("olleh") should be("hello")
    Update.operation[Person](_.copy(name = "Richard Dawkins")).update(darwin) should be(darwin.copy(name = "Richard Dawkins"))
  }

  "Field update" should "work" in {
    Person.Fields.name.updateVal("Richard Dawkins").update(darwin) should be(darwin.copy(name = "Richard Dawkins"))
    Person.Fields.age.updateWith(_ / 2).update(darwin) should be(darwin.copy(age = 25))
  }

  "Tree update" should "work on two fields" in {
    val f: Update.TreeU[Person] = Person.Fields.name.updateVal("Richard Dawkins") |@| Person.Fields.age.updateVal(80)
    f.update(darwin) should be(darwin.copy(name = "Richard Dawkins", age = 80))
  }

  it should "work on more than two fields" in {
    val f: Update.TreeU[Person] = Person.Fields.name.updateVal("Richard Dawkins") |@| Person.Fields.age.updateVal(80) |@| Person.Fields.bearded.updateVal(false)
    f.update(darwin) should be(darwin.copy(name = "Richard Dawkins", age = 80, bearded = false))
  }

}
