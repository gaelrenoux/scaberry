package scaberry.tests

import org.scalatest.{FlatSpec, Matchers}
import scaberry.tests.data.Dog

class BerryPropSpec extends FlatSpec with Matchers {

  "the prop" should "be created when declared" in {
    "Dog.meta.fields.name.label" should compile
    "Dog.meta.fields.name.other" should compile
  }

  it should "not be created when not declared" in {
    "Dog.meta.fields.name.what" shouldNot typeCheck
    "Dog.meta.fields.color.label" shouldNot typeCheck
  }

  it should "contain the correct value" in {
    Dog.meta.fields.name.label should be("Pet's name")
    Dog.meta.fields.name.other should be("Other")
  }


}

