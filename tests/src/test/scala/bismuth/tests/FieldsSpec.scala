package bismuth.tests

import bismuth.core.{Field, Fields}
import bismuth.tests.Models.{Person, _}
import org.scalatest.{FlatSpec, Matchers}

import scala.reflect.ClassTag

class FieldsSpec extends FlatSpec with Matchers with Helpers {
  val batman = Person("Bruce Wayne", 28)

  val animalValFields = Fields.fromVals[Animal]
  val dogValFields = Fields.fromVals[Dog]
  val dogConstructorFields = Fields.fromConstructor[Dog]

  "fromVals" should "return the public vals" in {
    "animalValFields.name" should compile
    "animalValFields.weight" should compile
    "animalValFields.color" should compile
    "dogValFields.name" should compile
    "dogValFields.weight" should compile
    "dogValFields.color" should compile
    "dogValFields.owner" should compile
  }

  it should "not return the non-public vals" in {
    "animalValFields.animate" shouldNot typeCheck
    "dogValFields.animate" shouldNot typeCheck
    "dogValFields.genus" shouldNot typeCheck
  }

  it should "not return the vars" in {
    "animalValFields.whatever" shouldNot typeCheck
    "dogValFields.whatever" shouldNot typeCheck
    "dogValFields.whatever2" shouldNot typeCheck
  }

  it should "not return the defs" in {
    "animalValFields.otherName" shouldNot typeCheck
    "dogValFields.otherName" shouldNot typeCheck
    "dogValFields.otherOtherName" shouldNot typeCheck
  }

  "fromConstructor" should "return the fields in the primary constructor" in {
    "dogConstructorFields.name" should compile
    "dogConstructorFields.weight" should compile
    "dogConstructorFields.color" should compile
    "dogConstructorFields.owner" should compile
  }

  it should "not return fields from another constructor" in {
    "dogConstructorFields.other" shouldNot typeCheck
  }

  it should "not return other vals" in {
    "dogConstructorFields.animate" shouldNot typeCheck
    "dogConstructorFields.owner" shouldNot typeCheck
    "dogConstructorFields.genus" shouldNot typeCheck
  }

  it should "not return the vars" in {
    "dogConstructorFields.whatever" shouldNot typeCheck
    "dogConstructorFields.whatever2" shouldNot typeCheck
  }

  it should "not return the defs" in {
    "dogConstructorFields.otherName" shouldNot typeCheck
    "dogConstructorFields.otherOtherName" shouldNot typeCheck
  }

  "Definitions" should "have the correct class" in {
    "val x: Field[Person, String] = animalValFields.name" should compile
    "val x: Field[Person, Long] = animalValFields.weight" should compile
    "val x: Field[Person, String] = dogValFields.name" should compile
    "val x: Field[Person, Long] = dogValFields.weight" should compile
    "val x: Field[Person, String] = dogValFields.owner" should compile
    "val x: Field[Person, String] = dogConstructorFields.name" should compile
    "val x: Field[Person, Long] = dogConstructorFields.weight" should compile
  }

  they should "carry the correct name" in {
    force.field(animalValFields.name).name should be("name")
    force.field(animalValFields.weight).name should be("weight")
    force.field(dogValFields.name).name should be("name")
    force.field(dogValFields.weight).name should be("weight")
    force.field(dogValFields.owner).name should be("owner")
    force.field(dogConstructorFields.name).name should be("name")
    force.field(dogConstructorFields.weight).name should be("weight")
  }

  they should "carry the correct class tags" in {
    force.field(animalValFields.name).sourceClassTag should be(implicitly[ClassTag[Person]])
    force.field(animalValFields.name).typeClassTag should be(implicitly[ClassTag[String]])
    force.field(animalValFields.weight).sourceClassTag should be(implicitly[ClassTag[Person]])
    force.field(animalValFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
    force.field(dogValFields.name).sourceClassTag should be(implicitly[ClassTag[Person]])
    force.field(dogValFields.name).typeClassTag should be(implicitly[ClassTag[String]])
    force.field(dogValFields.weight).sourceClassTag should be(implicitly[ClassTag[Person]])
    force.field(dogValFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
    force.field(dogValFields.owner).sourceClassTag should be(implicitly[ClassTag[Person]])
    force.field(dogValFields.owner).typeClassTag should be(implicitly[ClassTag[String]])
    force.field(dogConstructorFields.name).sourceClassTag should be(implicitly[ClassTag[Person]])
    force.field(dogConstructorFields.name).typeClassTag should be(implicitly[ClassTag[String]])
    force.field(dogConstructorFields.weight).sourceClassTag should be(implicitly[ClassTag[Person]])
    force.field(dogConstructorFields.weight).typeClassTag should be(implicitly[ClassTag[Long]])
  }


  val casimir: Animal = new Animal {
    override val weight: Long = 42L
    override val color: String = "orange"
    override val name: Option[String] = Some("Casimir")
  }
  val rex: Dog = Dog("brown", 9L, Some("Rex"))

  "Getters" should "return the correct value" in {
    force.field[Animal, String](animalValFields.name)(casimir).get should be(Some("Casimir"))
    force.field[Animal, Long](animalValFields.weight)(casimir).get should be(42L)
    force.field[Animal, String](animalValFields.name)(rex).get should be(Some("Rex"))
    force.field[Animal, Long](animalValFields.weight)(rex).get should be(9L)
    force.field[Animal, String](dogValFields.name)(rex).get should be(Some("Rex"))
    force.field[Animal, Long](dogValFields.weight)(rex).get should be(9L)
    force.field[Animal, Long](dogValFields.owner)(rex).get should be("Unknown")
    force.field[Animal, String](dogConstructorFields.name)(rex).get should be(Some("Rex"))
    force.field[Animal, Long](dogConstructorFields.weight)(rex).get should be(42L)
  }
}
